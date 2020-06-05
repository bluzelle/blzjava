package com.bluzelle.json;

class Writer {

    static void writeObject(StringBuilder builder, Object value, boolean sanitize) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof JsonObject) {
            ((JsonObject) value).write(builder, sanitize);
        } else if (value instanceof JsonArray) {
            ((JsonArray) value).write(builder, sanitize);
        } else if (value instanceof Boolean || value instanceof Integer) {
            builder.append(value);
        } else {
            builder.append("\"");
            writeString(builder, (String) value, sanitize);
            builder.append("\"");
        }
    }

    static void writeString(StringBuilder builder, String string, boolean sanitize) {
        int length = string.length();
        int i = 0;
        while (i < length) {
            char c = string.charAt(i++);
            if (sanitize) {
                switch (c) {
                    case '&':
                    case '<':
                    case '>':
                        builder.append("\\u00");
                        builder.append(String.format("%02x", (int) c)); // lowercase!
                        continue;
                }
            }
            switch (c) {
                // case '\'': // non-canonical
                case '"':
                case '\\':
                    builder.append('\\').append(c);
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
                case '\b':
                    builder.append('\\').append('b');
                    break;
                case '\f':
                    builder.append('\\').append('f');
                    break;
                default:
                    builder.append(c);
            }
        }
    }
}