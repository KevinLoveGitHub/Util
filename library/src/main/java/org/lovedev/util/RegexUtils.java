package org.lovedev.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin
 * @data 2018/5/17
 */
public class RegexUtils {

    private RegexUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isboolIp(String ipAddress) {
        String ip = "([1-9]|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])(//.(//d|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}
