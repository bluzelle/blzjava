// usage:
//    JsonArray jsonArray = new JsonArray();
//    jsonArray.put(jsonObject);
//    jsonArray.put(jsonArray);
//    jsonArray.put(valueString);
//    jsonArray.put(null);
//    jsonArray.put(5); // any other value will be converted to String
//    int length = jsonArray.length();
//    JsonObject jsonObject = jsonArray.getObject(index);
//    JsonArray jsonArray = jsonArray.getArray(index);
//    String string = jsonArray.getString(index);
//    int i = jsonArray.getInt(index);
//    String jsonString = jsonArray.toString();
package space.aqoleg.json;

import java.util.ArrayList;

public class JsonArray {
    private final ArrayList<Object> list = new ArrayList<>();

    static JsonArray parse(Parser parser) {
        JsonArray jsonArray = new JsonArray();
        while (true) {
            if (parser.nextChar() == ']') {
                return jsonArray;
            }
            parser.moveBack();

            jsonArray.list.add(parser.nextValue());

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
     * @throws ClassCastException        if value is not a JsonObject
     */
    public JsonObject getObject(int index) {
        Object object = list.get(index);
        if (object == null) {
            return null;
        } else if (object instanceof JsonObject) {
            return (JsonObject) object;
        } else {
            throw new ClassCastException("not a JsonObject " + object.toString());
        }
    }

    /**
     * @param index number of the value
     * @return JsonArray with this number or null
     * @throws IndexOutOfBoundsException if index is incorrect
     * @throws ClassCastException        if value is not a JsonArray
     */
    @SuppressWarnings("WeakerAccess")
    public JsonArray getArray(int index) {
        Object object = list.get(index);
        if (object == null) {
            return null;
        } else if (object instanceof JsonArray) {
            return (JsonArray) object;
        } else {
            throw new ClassCastException("not a JsonArray " + object.toString());
        }
    }

    /**
     * @param index number of the value
     * @return String with this number or null
     * @throws IndexOutOfBoundsException if index is incorrect
     * @throws ClassCastException        if value is not a String
     */
    public String getString(int index) {
        Object object = list.get(index);
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else {
            throw new ClassCastException("not a String " + object.toString());
        }
    }

    /**
     * @param index number of the value
     * @return integer with this number
     * @throws IndexOutOfBoundsException if index is incorrect
     * @throws ClassCastException        if value is not an integer
     */
    @SuppressWarnings("WeakerAccess")
    public int getInt(int index) {
        Object object = list.get(index);
        if (object instanceof String) {
            try {
                return Integer.parseInt((String) object);
            } catch (NumberFormatException e) {
                throw new ClassCastException("not an integer " + object.toString());
            }
        } else {
            throw new ClassCastException("not an integer " + object.toString());
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
        write(builder, false);
        return builder.toString();
    }

    void write(StringBuilder builder, boolean sanitize) {
        builder.append("[");
        boolean first = true;
        for (Object value : list) {
            if (!first) {
                builder.append(",");
            }
            first = false;
            Writer.writeObject(builder, value, sanitize);
        }
        builder.append("]");
    }
}