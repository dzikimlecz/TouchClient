package me.dzikimlecz.touchclient.client;

import me.dzikimlecz.touchclient.client.ServerHandler;
import me.dzikimlecz.touchclient.model.Message;
import me.dzikimlecz.touchclient.model.UserProfile;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.time.LocalDateTime.now;

class ServerHandlerTest {
    private final @NotNull UserProfile profile = UserProfile.of("LilPope", 2137);
    private final @NotNull UserProfile profile2 = UserProfile.of("BigPriest", 3000);
    private final @NotNull UserProfile profile3 = UserProfile.of("NOOOPE", 3000);
    ServerHandler handler = new ServerHandler(profile);

    @Test
    @DisplayName("Should write shit into caches")
    void name() {
        // Given
        var list = List.of(
                new Message(profile2, profile, "m1", now()),
                new Message(profile, profile3, "m2", now()),
                new Message(profile3, profile, "m3", now()),
                new Message(profile, profile2, "m4", now())
        );
        // When
        handler.writeIntoCaches(list);
        // Then
        while (true);
    }
}
