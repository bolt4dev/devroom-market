package com.bolt.devroom.util;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static final Map<UUID, Instant> cooldownMap = new HashMap<>();

    public static void setCooldown(UUID key, Duration duration) {
        cooldownMap.put(key, Instant.now().plus(duration));
    }

    public static boolean hasCooldown(UUID key) {
        Instant cooldown = cooldownMap.get(key);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    public static Instant removeCooldown(UUID key) {
        return cooldownMap.remove(key);
    }

    public static Duration getRemainingCooldown(UUID key) {
        Instant cooldown = cooldownMap.get(key);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            return Duration.ZERO;
        }
    }
}
