package me.dzikimlecz.touchclient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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


    public static final UserProfile NULL_USER = new UserProfile("", 0) {
        @Override
        public boolean equals(Object o) {
            return o == this;
        }
    };

}
