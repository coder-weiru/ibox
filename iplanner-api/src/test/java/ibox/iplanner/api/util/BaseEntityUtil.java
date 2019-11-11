package ibox.iplanner.api.util;

import ibox.iplanner.api.model.Frequency;
import ibox.iplanner.api.model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class BaseEntityUtil {

    public static Boolean randomBoolean() {
        return Math.random() < 0.5;
    }

    public static String anyTitle() {
        return RandomStringUtils.random(10, true, false);
    }

    public static String anyType() {
        return RandomStringUtils.random(10, true, false);
    }

    public static String anySummary() {
        return RandomStringUtils.random(30, true, false);
    }

    public static String anyDescription() {
        return RandomStringUtils.random(100, true, true);
    }

    public static String anyStatus() {
        return RandomStringUtils.random(5, true, false);
    }

    public static String anyActivityId() {
        return RandomStringUtils.random(10, true, false);
    }

    public static String anyLocation() {
        return RandomStringUtils.random(20, true, false);
    }

    public static Frequency anyFrequency() {
        return Frequency.DAILY;
    }

    public static Instant anyCreatedTime() {
        return Instant.now().minusMillis(new Random().nextInt(1000000));
    }

    public static Instant anyUpdatedTime() {
        return Instant.now().minusMillis(new Random().nextInt(500000));
    }

    public static Instant anyStartTime() {
        return Instant.now().minusMillis(new Random().nextInt(100));
    }

    public static Instant anyEndTime() {
        return Instant.now().plusMillis(new Random().nextInt(1000000));
    }

    public static String anyUUID() {
        return UUID.randomUUID().toString();
    }

    public static String anyShortId() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    public static String anyDisplayName() {
        return RandomStringUtils.random(20, true, false);
    }

    public static String anyEmail() {
        return RandomStringUtils.random(20, true, true) + "@gmail.com";
    }

    public static User anyUser() {
        User creator = new User();
        creator.setId(anyUUID());
        creator.setDisplayName(anyDisplayName());
        creator.setEmail(anyEmail());
        creator.setSelf(randomBoolean());
        return creator;
    }
}
