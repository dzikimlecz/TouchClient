package me.dzikimlecz.touchclient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class UserProfile {
    private String username;
    private long userTag;

    public String getUsername() {
        return username;
    }

    public long getUserTag() {
        return userTag;
    }

    @Contract(pure = true)
    @JsonIgnore
    public @NotNull String getNameTag() {
        return username + '#' + userTag;
    }

    @Contract(pure = true)
    @JsonIgnore
    public @NotNull String getUriNameTag() {
        return username + '_' + userTag;
    }

    private UserProfile(@NotNull String username, long userTag) {
        this.username = username;
        this.userTag = userTag;
    }

    public UserProfile() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserTag(long userTag) {
        this.userTag = userTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != UserProfile.class) return false;
        UserProfile that = (UserProfile) o;
        return userTag == that.userTag && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, userTag);
    }

    @Override
    @Contract(pure = true)
    public String toString() {
        return String.format("UserProfile{username='%s', userTag=%d}", username, userTag);
    }


    @Contract("_, _ -> new")
    public static @NotNull UserProfile of(@NotNull String username, long userTag) {
        if (username.isEmpty())
            throw new IllegalArgumentException("Username can't be empty.");
        if (userTag == 0)
            throw new IllegalArgumentException("User's tag can't be set to 0.");
        return new UserProfile(username, userTag);
    }

    // validates the array (should be exactly of format {name, tag}) and returns the name
    public static String getUsername(String @NotNull [] nameTagArr) {
        switch (nameTagArr.length) {
            case 0:
                throw new IllegalArgumentException("NameTag can't be empty.");
            case 1:
                throw new IllegalArgumentException("'" + nameTagArr[0] + "' is not a valid NameTag.");
            case 2:
                return nameTagArr[0];
            default:
                throw new IllegalArgumentException(String.join("#", nameTagArr) + " is not a valid NameTag.");
        }
    }

    public static long parseTag(@NotNull String tagString) {
        final long tag;
        final Supplier<RuntimeException> exception =
                () -> new IllegalArgumentException("'" + tagString + "' is not a valid tag.");
        try {
            tag = Long.parseLong(tagString);
        } catch (NumberFormatException e) {
            throw exception.get();
        }
        if (tag == 0) throw exception.get();
        return tag;
    }


    public static final UserProfile NULL_USER = new UserProfile("", 0) {
        @Override
        public boolean equals(Object o) {
            return o == this;
        }
    };

}
