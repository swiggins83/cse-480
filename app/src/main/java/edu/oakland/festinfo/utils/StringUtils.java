package edu.oakland.festinfo.utils;

/**
 * Created by steven on 9/24/15.
 */
public class StringUtils {

    public static String capitalizeFirstLetter(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}
