package com.atom.lib.tinysecurity;

final class TinySecurityCore {
    static {
        System.loadLibrary("tinySecurity");
        init();
    }

    private TinySecurityCore() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    static String get(String key) {
        return getString(key);
    }

    private static native void init();

    private static native String getString(String key);

    public static native byte[] decrypt(byte[] key, byte[] cipher);
}
