package space.aqoleg.json;

import space.aqoleg.exception.JsonException;

class JsonParser {
    private final String source;
    private int index = 0;

    JsonParser(String source) {
        this.source = source;
    }

    // returns next non-whitespace char
    char nextChar() {
        try {
            while (true) {
                char c = source.charAt(index++);
                // skip space(32), linefeed(10), carriage return(13), horizontal tab(9)
                if (c == 9 || c == 10 || c == 13 || c == 32) {
                    continue;
                }
                return c;
            }
        } catch (IndexOutOfBoundsException e) {
            throw exception("unclosed");
        }
    }

    void moveBack() {
        index--;
    }

    String nextKey() {
        if (nextChar() != '"') {
            throw exception("not a key");
        }
        String key = parseString();
        if (key.isEmpty()) {
            throw exception("empty key");
        }
        return key;
    }

    // returns null, String, JsonObject or JsonArray
    Object nextObject() {
        char c = nextChar();
        switch (c) {
            case '"':
                return parseString();
            case '{':
                return JsonObject.parse(this);
            case '[':
                return JsonArray.parse(this);
            default:
                // number, true, false or null
                StringBuilder builder = new StringBuilder();
                try {
                    while ("truefalsenull+-0123456789.Ee".indexOf(c) >= 0) {
                        builder.append(c);
                        c = source.charAt(index++);
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw exception("unclosed");
                }
                index--;
                String s = builder.toString();
                if (s.isEmpty()) {
                    throw exception("no value");
                }
                if (s.equals("null")) {
                    return null;
                }
                return s;
        }
    }

    JsonException exception(String message) {
        return new JsonException(source + " at index " + (index - 1) + ": " + message);
    }

    private String parseString() {
        StringBuilder builder = new StringBuilder();
        try {
            while (true) {
                char c = source.charAt(index++);
                switch (c) {
                    case '"':
                        return builder.toString();
                    case '\\':
                        c = source.charAt(index++);
                        switch (c) {
                            case '"':
                            case '\\':
                            case '/':
                                builder.append(c);
                                break;
                            case 'b':
                                builder.append('\b');
                                break;
                            case 'f':
                                builder.append('\f');
                                break;
                            case 'n':
                                builder.append('\n');
                                break;
                            case 'r':
                                builder.append('\r');
                                break;
                            case 't':
                                builder.append('\t');
                                break;
                            case 'u':
                                char[] chars = new char[4];
                                for (int i = 0; i < 4; i++) {
                                    chars[i] = source.charAt(index++);
                                }
                                builder.append((char) Integer.parseInt(new String(chars), 16));
                                break;
                            default:
                                throw exception("incorrect char \\" + c);
                        }
                        break;
                    default:
                        builder.append(c);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw exception("unclosed");
        } catch (NumberFormatException e) {
            throw exception("incorrect char \\u");
        }
    }
}