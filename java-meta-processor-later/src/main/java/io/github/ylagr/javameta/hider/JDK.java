package io.github.ylagr.javameta.hider;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author suiwp
 * @date 2025/9/19 10:49
 */
public class JDK {
    private final static int Version;

    static {
        String version = System.getProperty("java.specification.version");
        if (version.startsWith("1.")) {
            version = version.substring(2);
        }
        Version = Integer.parseInt(version);
    }

    public static void breakSecurity() {
        Unsafe unsafe = null;
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean is8() {
        return Version == 8;
    }

    public static boolean is9() {
        return Version == 9;
    }

    public static boolean is9orLater() {
        return Version >= 9;
    }

    public static boolean is11orLater() {
        return Version >= 11;
    }

    public static boolean is17orLater() {
        return Version >= 17;
    }

    public static boolean is15orEarlier() {
        return Version <= 15;
    }
}
