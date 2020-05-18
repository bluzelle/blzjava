// integer and boolean always represents as string
// usage:
//    JsonObject jsonObject = new JsonObject();
//    JsonObject jsonObject = JsonObject.parse(jsonString);
//    jsonObject.put(keyString, jsonObject);
//    jsonObject.put(keyString, jsonArray);
//    jsonObject.put(keyString, valueString);
//    jsonObject.put(keyString, null);
//    jsonObject.put(keyString, 5); // any other value will be used as String
//    JsonObject jsonObject = jsonObject.getObject(keyString);
//    JsonArray jsonArray = jsonObject.getArray(keyString);
//    String string = jsonObject.getString(keyString);
//    int i = jsonObject.getInt(keyInt);
//    String jsonString = jsonObject.toString();
//    String sanitizedString = jsonObject.toSanitizeString();
package space.aqoleg.json;

import java.util.Map;
import java.util.TreeMap;

public class JsonObject {
    private final TreeMap<String, Object> map = new TreeMap<>(); // <key, value>

    /**
     * @param source String to be parsed
     * @return JsonObject created from this source
     * @throws NullPointerException if source == null
     * @throws JsonException        if source is incorrect
     */
    public static JsonObject parse(String source) {
        Parser parser = new Parser(source);
        if (parser.nextChar() != '{') {
            throw parser.exception("incorrect first char");
        }
        return parse(parser);
    }

    static JsonObject parse(Parser parser) {
        JsonObject jsonObject = new JsonObject();
        while (true) {
            if (parser.nextChar() == '}') {
                return jsonObject;
            }
            parser.moveBack();

            String key = parser.nextKey();
            if (jsonObject.map.containsKey(key)) {
                throw parser.exception("duplicate key");
            }

            if (parser.nextChar() != ':') {
                throw parser.exception("no ':' after key");
            }

            jsonObject.map.put(key, parser.nextValue());

            switch (parser.nextChar()) {
                case ',':
                    break;
                case '}':
                    return jsonObject;
                default:
                    throw parser.exception("unexpected symbol");
            }
        }
    }

    /**
     * @param key key
     * @return JsonObject associated with this key or null
     * @throws NullPointerException if key == null
     * @throws JsonException        if value is not a JsonObject or null
     */
    public JsonObject getObject(String key) {
        Object object = map.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof JsonObject) {
            return (JsonObject) object;
        } else {
            throw new JsonException("not a JsonObject");
        }
    }

    /**
     * @param key key
     * @return JsonArray associated with this key or null
     * @throws NullPointerException if key == null
     * @throws JsonException        if value is not a JsonArray or null
     */
    public JsonArray getArray(String key) {
        Object object = map.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof JsonArray) {
            return (JsonArray) object;
        } else {
            throw new JsonException("not a JsonArray");
        }
    }

    /**
     * @param key key
     * @return String associated with this key or null
     * @throws NullPointerException if key == null
     * @throws JsonException        if value is not a String or null
     */
    public String getString(String key) {
        Object object = map.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else {
            throw new JsonException("not a String");
        }
    }

    /**
     * @param key key
     * @return integer associated with this key
     * @throws NullPointerException if key == null
     * @throws JsonException        if value is not an integer
     */
    public int getInt(String key) {
        Object object = map.get(key);
        if (object instanceof String) {
            try {
                return Integer.parseInt((String) object);
            } catch (NumberFormatException e) {
                throw new JsonException("not an integer " + e.getMessage());
            }
        } else {
            throw new JsonException("not a String");
        }
    }

    /**
     * put pair {"key":value} in this JsonObject, or rewrite if JsonObject already contains this key
     *
     * @param key   key
     * @param value null, JsonObject, JsonArray; if any other object, uses toString()
     * @return this
     * @throws NullPointerException if key == null
     */
    public JsonObject put(String key, Object value) {
        if (value != null && !(value instanceof JsonObject) && !(value instanceof JsonArray)) {
            value = value.toString();
        }
        map.put(key, value);
        return this;
    }

    /**
     * @return String representation of this JsonObject in alphabet order, without whitespaces
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        write(builder, false);
        return builder.toString();
    }

    /**
     * @return String representation of this JsonObject in alphabet order, without whitespaces, &<> replaced with /u
     */
    public String toSanitizeString() {
        StringBuilder builder = new StringBuilder();
        write(builder, true);
        return builder.toString();
    }

    void write(StringBuilder builder, boolean sanitize) {
        builder.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            builder.append("\"");
            Writer.writeString(builder, entry.getKey(), sanitize);
            builder.append("\"");
            builder.append(":");
            Writer.writeObject(builder, entry.getValue(), sanitize);
        }
        builder.append("}");
    }
}