// usage:
//    JsonArray jsonArray = new JsonArray();
//    jsonArray.put(valueString);
//    jsonArray.put(jsonArray);
//    jsonArray.put(jsonObject);
//    jsonArray.put(null);
//    jsonArray.put(5); // any other value will be used as String
//    int length = jsonArray.length();
//    String string = jsonArray.getString(index);
//    JsonArray jsonArray = jsonArray.getArray(index);
//    JsonObject jsonObject = jsonArray.getObject(index);
//    String jsonString = jsonArray.toString();
package space.aqoleg.json;

import space.aqoleg.utils.ParseException;

import java.util.ArrayList;

public class JsonArray {
    private final ArrayList<Object> list = new ArrayList<>();

    static JsonArray parse(Parser parser) {
        JsonArray jsonArray = new JsonArray();
        if (parser.nextChar() == ']') {
            return jsonArray;
        }
        parser.moveBack();

        while (true) {
            jsonArray.list.add(parser.nextObject());

            switch (parser.nextChar()) {
                case ',':
                    break;
                case ']':
                    return jsonArray;
                default:
                    throw parser.exception("unexpected symbol");
            }
        }
    }

    /**
     * @return number of values in this JsonArray
     */
    public int length() {
        return list.size();
    }

    /**
     * @param index number of the value
     * @return JsonObject with this number or null
     * @throws IndexOutOfBoundsException if index is incorrect
     * @throws ParseException            if object is not a JsonObject or null
     */
    public JsonObject getObject(int index) {
        Object object = list.get(index);
        if (object == null) {
            return null;
        } else if (object instanceof JsonObject) {
            return (JsonObject) object;
        } else {
            throw new ParseException("not a JsonObject");
        }
    }

    /**
     * @param index number of the value
     * @return JsonArray with this number or null
     * @throws IndexOutOfBoundsException if index is incorrect
     * @throws ParseException            if object is not a JsonArray or null
     */
    public JsonArray getArray(int index) {
        Object object = list.get(index);
        if (object == null) {
            return null;
        } else if (object instanceof JsonArray) {
            return (JsonArray) object;
        } else {
            throw new ParseException("not a JsonArray");
        }
    }

    /**
     * @param index number of the value
     * @return String with this number or null
     * @throws IndexOutOfBoundsException if index is incorrect
     * @throws ParseException            if object is not a String or null
     */
    public String getString(int index) {
        Object object = list.get(index);
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else {
            throw new ParseException("not a String");
        }
    }

    /**
     * put object in this JsonArray
     *
     * @param value null, JsonObject, JsonArray; if any other object, use toString()
     * @return this
     */
    public JsonArray put(Object value) {
        if (value != null && !(value instanceof JsonObject) && !(value instanceof JsonArray)) {
            value = value.toString();
        }
        list.add(value);
        return this;
    }

    /**
     * @return String representation of this JsonArray without whitespaces
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        append(builder);
        return builder.toString();
    }

    void append(StringBuilder builder) {
        builder.append("[");
        boolean first = true;
        for (Object value : list) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            Writer.writeObject(builder, value);
        }
        builder.append("]");
    }
}