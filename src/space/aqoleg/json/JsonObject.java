// integer and boolean always represents as string
// usage:
//    JsonObject jsonObject = JsonObject.parse(jsonString);
//    JsonObject jsonObject = new JsonObject();
//    jsonObject.put(keyString, valueString);
//    jsonObject.put(keyString, jsonArray);
//    jsonObject.put(keyString, jsonObject);
//    jsonObject.put(keyString, null);
//    jsonObject.put(keyString, 5); // any other value will be used as String
//    String string = jsonObject.getString(keyString);
//    JsonArray jsonArray = jsonObject.getArray(keyString);
//    JsonObject jsonObject = jsonObject.getObject(keyString);
//    String jsonString = jsonObject.toString();
package space.aqoleg.json;

import space.aqoleg.utils.ParseException;

import java.util.Map;
import java.util.TreeMap;

public class JsonObject {
    private final TreeMap<String, Object> map = new TreeMap<>();

    /**
     * @param source String to be parsed
     * @return JsonObject created from this source
     * @throws NullPointerException if source == null
     * @throws ParseException       if source is incorrect
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
        if (parser.nextChar() == '}') {
            return jsonObject;
        }
        parser.moveBack();

        while (true) {
            String key = parser.nextKey();
            if (jsonObject.map.containsKey(key)) {
                throw parser.exception("duplicate key");
            }

            if (parser.nextChar() != ':') {
                throw parser.exception("no ':' after key");
            }

            jsonObject.map.put(key, parser.nextObject());

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
     * @throws ParseException       if object is not a JsonObject or null
     */
    public JsonObject getObject(String key) {
        Object object = map.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof JsonObject) {
            return (JsonObject) object;
        } else {
            throw new ParseException("not a JsonObject");
        }
    }

    /**
     * @param key key
     * @return JsonArray associated with this key or null
     * @throws NullPointerException if key == null
     * @throws ParseException       if object is not a JsonArray or null
     */
    public JsonArray getArray(String key) {
        Object object = map.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof JsonArray) {
            return (JsonArray) object;
        } else {
            throw new ParseException("not a JsonArray");
        }
    }

    /**
     * @param key key
     * @return String associated with this key or null
     * @throws NullPointerException if key == null
     * @throws ParseException       if object is not a String or null
     */
    public String getString(String key) {
        Object object = map.get(key);
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else {
            throw new ParseException("not a String");
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
        append(builder);
        return builder.toString();
    }

    void append(StringBuilder builder) {
        builder.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            builder.append("\"");
            builder.append(entry.getKey());
            builder.append("\"");
            builder.append(":");
            Writer.writeObject(builder, entry.getValue());
        }
        builder.append("}");
    }
}