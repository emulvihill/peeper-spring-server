package com.snazzyrobot.peeper.utility;

public class PatternUtil {
    public static String stripBase64DataUriPrefix(String dataUri) {
        return dataUri.replaceAll(".*base64,", "");
    }
}
