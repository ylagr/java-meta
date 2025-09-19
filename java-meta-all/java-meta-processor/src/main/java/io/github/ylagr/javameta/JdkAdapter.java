package io.github.ylagr.javameta;

/**
 * @author suiwp
 * @date 2025/9/19 11:08
 */
public class JdkAdapter {
    static {
        JDK.breakSecurity();
    }
    public static void invoke(){
        // empty function , just to invoke static block
    }
}
