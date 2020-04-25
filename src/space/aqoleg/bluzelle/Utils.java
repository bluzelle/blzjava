package space.aqoleg.bluzelle;

import space.aqoleg.exception.UtilException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class Utils {
    static String makeRandomString() {
        StringBuilder out = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int charactersLength = characters.length();
        for (int i = 0; i < 32; i++) {
            out.append(characters.charAt((int) (Math.random() * charactersLength)));
        }
        return out.toString();
    }

    static String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UtilException(e.getMessage());
        }
    }

    static int parse(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new UtilException(e.getMessage());
        }
    }
}