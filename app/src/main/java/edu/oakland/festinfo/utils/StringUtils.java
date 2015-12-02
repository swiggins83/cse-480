package edu.oakland.festinfo.utils;

/**
 * Created by steven on 9/24/15.
 */
public class StringUtils {

    public static String capitalizeFirstLetter(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String toTitleCase(String s) {
        String capString = "";
        String[] split = s.split(" ");
        for (int i=0; i < split.length; i++) {
            capString += Character.toUpperCase(split[i].charAt(0)) + split[i].substring(1);
            capString += (i == split.length - 1) ? "" : " ";
        }
        return capString;
    }

}
