package me.dzikimlecz.touchclient.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class Env {
    private static Dotenv env;

    public static @Nullable String getEnv(@NotNull String key) {
        return env.get(key.toUpperCase(Locale.ROOT).replace(' ', '_'));
    }

    public static void load() {
        env = Dotenv.load();
    }

}
