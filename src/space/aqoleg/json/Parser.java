package space.aqoleg.json;

class Parser {
    private final String source;
    private int index = 0;

    Parser(String source) {
        this.source = source;
    }

    // returns next non-whitespace char
    char nextChar() {
        try {
            while (true) {
                char c = source.charAt(index++);
                // skip space ' ' (32), new line '\n' (10), carriage return '\r' (13), horizontal tab '\t' (9)
                if (c == 32 || c == 10 || c == 13 || c == 9) {
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
        String key;
        char c = nextChar();
        if (c == '"' || c == '\'') {
            key = parseString(c);
        } else {
            moveBack();
            key = parseUnquotedString();
        }

        if (key.isEmpty()) {
            throw exception("empty key");
        }
        return key;
    }

    // returns null, JsonObject, JsonArray, Boolean, Integer or String
    Object nextValue() {
        char c = nextChar();
        switch (c) {
            case '"':
            case '\'':
                return parseString(c);
            case '{':
                return JsonObject.parse(this);
            case '[':
                return JsonArray.parse(this);
            default:
                moveBack();
                String value = parseUnquotedString();
                switch (value) {
                    case "null":
                        return null;
                    case "true":
                        return Boolean.TRUE;
                    case "false":
                        return Boolean.FALSE;
                    default:
                        try {
                            return Integer.parseInt(value);
                        } catch (NumberFormatException ignored) {
                            return value;
                        }
                }
        }
    }

    IllegalArgumentException exception(String message) {
        return new IllegalArgumentException(source + " at index " + (index - 1) + ": " + message);
    }

    private String parseString(char openChar) {
        StringBuilder builder = new StringBuilder();
        try {
            while (true) {
                char c = source.charAt(index++);
                if (c == openChar) {
                    return builder.toString();
                } else if (c == '\\') {
                    c = source.charAt(index++);
                    switch (c) {
                        case 'n':
                            builder.append('\n');
                            break;
                        case 'r':
                            builder.append('\r');
                            break;
                        case 't':
                            builder.append('\t');
                            break;
                        case 'b':
                            builder.append('\b');
                            break;
                        case 'f':
                            builder.append('\f');
                            break;
                        case 'u':
                            char[] chars = new char[4];
                            for (int i = 0; i < 4; i++) {
                                chars[i] = source.charAt(index++);
                            }
                            try {
                                builder.append((char) Integer.parseInt(new String(chars), 16));
                            } catch (NumberFormatException e) {
                                throw exception("incorrect char \\u");
                            }
                            break;
                        default:
                            builder.append(c);
                    }
                } else {
                    builder.append(c);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw exception("unclosed");
        }
    }

    private String parseUnquotedString() {
        StringBuilder builder = new StringBuilder();
        try {
            while (true) {
                char c = source.charAt(index++);
                switch (c) {
                    case ':':
                    case ',':
                    case ']':
                    case '}':
                        moveBack();
                        return builder.toString().trim();
                    default:
                        builder.append(c);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw exception("unclosed");
        }
    }
}