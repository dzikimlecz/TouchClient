package me.dzikimlecz.touchclient.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class UserProfile {
    private final String username;
    private final long userTag;

    public String getUsername() {
        return username;
    }

    public long getUserTag() {
        return userTag;
    }

    @Contract(pure = true)
    public @NotNull String getNameTag() {
        return username + '#' + userTag;
    }

    private UserProfile(@NotNull String username, long userTag) {
        this.username = username;
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

}
