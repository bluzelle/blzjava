package space.aqoleg.json;

class Writer {

    static void writeObject(StringBuilder builder, Object value) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof JsonObject) {
            ((JsonObject) value).append(builder);
        } else if (value instanceof JsonArray) {
            ((JsonArray) value).append(builder);
        } else {
            builder.append("\"");
            writeString(builder, (String) value);
            builder.append("\"");
        }
    }

    private static void writeString(StringBuilder builder, String string) {
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            switch (c) {
                case '"':
                case '\\':
                    // non-canonical use for signature
                    // case '/':
                    builder.append('\\').append(c);
                    break;
                case '\b':
                    builder.append('\\').append('b');
                    break;
                case '\f':
                    builder.append('\\').append('f');
                    break;
                case '\n':
                    builder.append('\\').append('n');
                    break;
                case '\r':
                    builder.append('\\').append('r');
                    break;
                case '\t':
                    builder.append('\\').append('t');
                    break;
                case '&':
                case '<':
                case '>':
                    builder.append("\\u00");
                    builder.append(String.format("%02X", (int) c)); // zero-padded, min width 2
                    break;
                default:
                    builder.append(c);
            }
        }
    }
}