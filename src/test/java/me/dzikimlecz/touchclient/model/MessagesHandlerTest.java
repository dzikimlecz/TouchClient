package me.dzikimlecz.touchclient.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

class MessagesHandlerTest {
    private final @NotNull UserProfile profile = UserProfile.of("LilPope", 2137);
    private final @NotNull UserProfile profile2 = UserProfile.of("BigPriest", 3000);
    private final @NotNull UserProfile profile3 = UserProfile.of("NOOOPE", 3000);
    MessagesHandler handler = new MessagesHandler(profile);

    @Test
    @DisplayName("Should write shit into caches")
    void name() {
        // Given
        var list = List.of(
                new Message(profile2, profile, "m1", now()),
                new Message(profile3, profile, "m2", now()),
                new Message(profile3, profile, "m3", now()),
                new Message(profile2, profile, "m4", now())
        );
        // When
        handler.writeIntoCaches(list);
        // Then
    }
}
