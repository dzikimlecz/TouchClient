package me.dzikimlecz.touchclient.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coresearch.cvurl.io.request.CVurl;
import me.dzikimlecz.touchclient.mainview.ConnectionException;
import me.dzikimlecz.touchclient.model.container.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.lang.Math.ceil;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static me.dzikimlecz.touchclient.config.Env.getEnv;

public final class MessagesHandler  {
    private final UserProfile profile;
    private final AtomicReference<UserProfile> lastRetrieved = new AtomicReference<>();
    private final AtomicInteger currentPageIndex = new AtomicInteger();
    private final CVurl cVurl = new CVurl();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private final File conversationsCache = new File(System.getenv("APPDATA") + "\\Touch");
    private Messages cachedConversation;

    private final static int loadAtOnce = 24;

    public MessagesHandler(@NotNull UserProfile profile) {
        this.profile = profile;
        conversationsCache.mkdirs();
    }

    public Optional<Messages> getConversation(UserProfile with, int page) {
        // fetches new messages if they're requested
        if (page == 0) {
            loadNew();
            cachedConversation = readCache(with).orElse(null);
        }
        // if cache holds other, or no conversation, load proper one into it
        if (cachedConversation == null || !with.equals(cachedConversation.getElements().get(0).getSender())) {
            // checks if conversation was cached.
            cachedConversation = readCache(with)
                    .orElseGet( () -> {
                        //checks if new messages contain the conversation
                        loadNew();
                        return readCache(with)
                                .orElseGet(() -> {
                                    // loads any older messages, or returns null if those don't exist
                                    try {
                                        loadOlder(with);
                                    } catch (NoSuchElementException e) {
                                        return null;
                                    }
                                    return readCache(with).orElse(null);
                                });
                    });
            // if failed to load conversation into cache returns empty;
        }
        if (cachedConversation == null) return Optional.empty();
        // loads more messages into caches until ordered page is reached
        var cachedElements = cachedConversation.getElements();
        while (cachedElements.size() < (page + 1) * loadAtOnce) {
            try {
                loadOlder(with);
            } catch (NoSuchElementException e) {
                // if there is no such page return empty
                if (cachedElements.size() < page * loadAtOnce)
                    return Optional.empty();
                //if page is not full return what was retrieved
                final var elements = cachedElements.subList(page * loadAtOnce, cachedElements.size());
                return Optional.of(new Messages(
                        page,
                        page + 1,
                        elements,
                        elements.size()
                ));
            }
            cachedConversation = readCache(with)
                    // if the conversation wasn't cached method wouldn't reach this point,
                    // or the cache would've been deleted while the run time
                    .orElseThrow(() -> new RuntimeException(new IOException("Cache was deleted concurrently to the program.")));
            cachedElements = cachedConversation.getElements();
        }
        // page is full, everything went all right
        return Optional.of(new Messages(
                page,
                (int) ceil(cachedElements.size() / (loadAtOnce * 1.0)),
                cachedElements.subList(page * loadAtOnce, (page + 1) * loadAtOnce),
                loadAtOnce
        ));
    }

    public void loadNew() {
        final var newMessages = fetchNew();
        final List<Message> newElements = newMessages.getElements();
        writeIntoCaches(newElements);
    }

    public void loadOlder(@NotNull UserProfile from) {
        if (!from.equals(lastRetrieved.get())) {
            currentPageIndex.set(0);
            lastRetrieved.set(from);
        }
        var request = new MessageRequestSpecification(
                List.of(getUserProfile().getUriNameTag(), from.getUriNameTag()),
                currentPageIndex.getAndIncrement(),
                loadAtOnce
        );
        var result = fetch(request);
        if (result.getElements().isEmpty())
            throw new NoSuchElementException("Already loaded everything.");
        writeIntoCaches(result.getElements());
    }

    void writeIntoCaches(@NotNull List<Message> newElements) {
        // Collect messages with every other user
        final Map<UserProfile, List<Message>> profileMessages = newElements.stream()
                .collect(toMap(
                        msg -> retrieveOtherProfile(msg).orElse(UserProfile.NULL_USER),
                        message -> {
                            final var list = new ArrayList<Message>();
                            list.add(message);
                            return list;
                        },
                        (messages, messages2) -> {
                            messages.addAll(messages2);
                            return messages;
                        },
                        HashMap::new
                ));
        profileMessages.remove(UserProfile.NULL_USER);
        // Create and collect caches of messages with every other user
        final Map<UserProfile, File> profileCaches = newElements.stream()
                .map(this::retrieveOtherProfile)
                .filter(Optional::isPresent)
                .distinct()
                .map(Optional::get)
                .collect(toMap(
                        Function.identity(),
                        profile -> new File(conversationsCache, profile.getUriNameTag() + ".touch")
                ));
        profileCaches.forEach((profile, cache) -> {
            final var messageList = profileMessages.get(profile);
            if (messageList == null) return;
            // checks whether the cache haven't been created yet and creates one if so.
            final boolean isCacheNew;
            try {
                isCacheNew = cache.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (isCacheNew) {
                // save messages
                final var messages = new Messages(0, 1, messageList, messageList.size());
                try {
                    objectMapper.writeValue(cache, messages);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // append messages to the already cached ones and save them
                final Messages messages;
                try {
                    messages = objectMapper.readValue(cache, Messages.class);
                } catch (IOException e) {
                    return;
                }
                // creates new list to be sure that its mutable
                final var elements = new ArrayList<>(messages.getElements());
                for (Message message : messageList)
                    if (message != null && !elements.contains(message))
                        elements.add(message);
                messages.setElements(elements);
                try {
                    objectMapper.writeValue(cache, messages);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            cache.deleteOnExit();
        });
    }

    private Messages fetchNew() {
        final var url = format("%s/msg/%s?page=%d&size=%d",
                getEnv("server address"), getUserProfile().getUriNameTag(), 0, loadAtOnce);
        var response = cVurl.get(url)
                .asString()
                .orElseThrow(ConnectionException::new)
                .getBody();
        try {
            return objectMapper.readValue(response, Messages.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Messages fetch(MessageRequestSpecification request) {
        final var url = format("%s/msg?user1=%s&user2=%s&page=%d&size=%d",
                getEnv("server address"),
                request.getNameTags().get(0),
                request.getNameTags().get(1),
                request.getPage(),
                request.getSize()
        );
        var response = cVurl.get(url)
                .asString()
                .orElseThrow(ConnectionException::new)
                .getBody();
        try {
            return objectMapper.readValue(response, Messages.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Messages> readCache(UserProfile of) {
        final var file = new File(conversationsCache, of.getUriNameTag() + ".touch");
        if (file.exists())
            try {
                return Optional.of(objectMapper.readValue(readText(file), Messages.class));
            } catch (IOException e) {
                return Optional.empty();
            }
        return Optional.empty();
    }

    @NotNull
    private UserProfile getUserProfile() {
        return profile;
    }

    @NotNull
    private static String readTextAssertTheFileExists(File file) {
        try {
            return readText(file);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @NotNull
    private static String readText(File file) throws IOException {
        final var str = new StringBuilder();
        try (var scanner = new Scanner(file) ) {
            while (scanner.hasNextLine())
                str.append(scanner.nextLine()).append('\n');
        }
        return str.toString();
    }

    // if message is sent or received by the user returns the other's profile, null otherwise
    private Optional<UserProfile> retrieveOtherProfile(Message msg) {
        if (profile.equals(msg.getRecipient()))
            return Optional.of(msg.getSender());
        else if (profile.equals(msg.getSender()))
            return Optional.of(msg.getRecipient());
        else return Optional.empty();
    }
}
