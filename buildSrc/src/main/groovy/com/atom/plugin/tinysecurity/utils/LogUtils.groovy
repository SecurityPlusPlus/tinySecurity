package com.atom.plugin.tinysecurity.utils
/**
 * LogUtils
 */
class LogUtils {

    static boolean debug = false

    static void Log(String msg) {
        if (debug) {
            println "********"
            println "$msg"
            println "********"
        }
    }
}