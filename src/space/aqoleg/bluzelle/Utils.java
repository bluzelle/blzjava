package space.aqoleg.bluzelle;

import space.aqoleg.utils.ParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Utils {
    public static String randomString() {
        StringBuilder out = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int charactersLength = characters.length();
        for (int i = 0; i < 32; i++) {
            out.append(characters.charAt((int) (Math.random() * charactersLength)));
        }
        return out.toString();
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            return string;
        }
    }

    static int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
    }
}