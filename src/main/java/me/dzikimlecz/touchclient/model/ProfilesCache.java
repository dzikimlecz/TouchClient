package me.dzikimlecz.touchclient.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toUnmodifiableList;
import static me.dzikimlecz.touchclient.model.UserProfile.getUsername;
import static me.dzikimlecz.touchclient.model.UserProfile.parseTag;

public class ProfilesCache {
    private static final File usersStorage = new File(System.getenv("APPDATA"),  "\\Touch\\.users#");

    private static final Set<UserProfile> profiles = new HashSet<>();

    static {
        usersStorage.mkdirs();
        try {
            profiles.addAll(
                    Files.walk(usersStorage.toPath())
                            .map(Path::toFile)
                            .map(File::getName)
                            .map(s -> s.substring(1))
                            .map(s -> {
                                final var strings = s.split("_");
                                return UserProfile.of(getUsername(strings), parseTag(strings[1]));
                            })
                            .collect(toUnmodifiableList())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void cacheUser(@NotNull UserProfile profile) {
        try {
            new File(usersStorage, "." + profile.getUriNameTag()).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profiles.add(profile);
    }

    public static void cacheUsers(@NotNull Collection<UserProfile> profiles) {
        try {
            for (UserProfile profile : profiles)
                new File(usersStorage, "." + profile.getUriNameTag()).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProfilesCache.profiles.addAll(profiles);
    }

    @Contract(" -> new")
    public static @NotNull List<UserProfile> getProfiles() {
        return new ArrayList<>(profiles);
    }
}
