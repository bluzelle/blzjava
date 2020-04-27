package space.aqoleg.json.test;

import org.junit.jupiter.api.Test;
import space.aqoleg.json.JsonArray;
import space.aqoleg.json.JsonObject;
import space.aqoleg.utils.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

    @Test
    void nextCharTest() {
        JsonObject json = JsonObject.parse("{\"one\":  \n \r \t    \"1\"}");
        assertEquals("{\"one\":\"1\"}", json.toString());
        // unclosed
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("")
        );
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{")
        );
    }

    @Test
    void nextKeyTest() {
        // not a key
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{one\":  }")
        );
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{{\"one\":")
        );
        // empty key
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"\":1")
        );
    }

    @Test
    void nextObjectTest() {
        JsonObject json = JsonObject.parse("{\"one\":null,\"two\":true,\"three\":-900.89,\"four\":8E461}");
        assertEquals("{\"four\":\"8E461\",\"one\":null,\"three\":\"-900.89\",\"two\":\"true\"}", json.toString());
        // no value
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\":  }")
        );
        // unclosed
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\": tr")
        );
    }

    @Test
    void parseStringTest() {
        // unclosed
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\":\"t")
        );
        // incorrect char
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\" \\g\"")
        );
        // incorrect char u
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\":\"r\\uuuuu\"")
        );
        JsonObject json = JsonObject.parse("{\"one\":\" \\\", \\\\, \\/ \"}");
        assertEquals(" \", \\, / ", json.getString("one"));
        // non-canonical use for signature
        // assertEquals("{\"one\":\" \\\", \\\\, \\/ \"}", json.toString());
        assertEquals("{\"one\":\" \\\", \\\\, / \"}", json.toString());

        json = JsonObject.parse("{\"one\":\" \\b, \\f, \\n, \\r, \\t \"}");
        assertEquals(" \b, \f, \n, \r, \t ", json.getString("one"));
        assertEquals("{\"one\":\" \\b, \\f, \\n, \\r, \\t \"}", json.toString());

        json = JsonObject.parse("{\"one\":\"\\u0048\"}");
        assertEquals("{\"one\":\"H\"}", json.toString());

        json = new JsonObject();
        json.put("one", ">,  < < \\ &&");
        assertEquals(">,  < < \\ &&", json.getString("one"));
        assertEquals("{\"one\":\"\\u003E,  \\u003C \\u003C \\\\ \\u0026\\u0026\"}", json.toString());
        json = JsonObject.parse(json.toString());
        assertEquals(">,  < < \\ &&", json.getString("one"));
    }

    @Test
    void objectParse() {
        assertThrows(
                NullPointerException.class,
                () -> JsonObject.parse(null)
        );
        // incorrect first char
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("r")
        );
        // duplicate key
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\":1,\"one\":1}")
        );
        // no ':' after key
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\"1}")
        );
        // unexpected symbol
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\":1:")
        );
    }

    @Test
    void objectGetObject() {
        JsonObject json = JsonObject.parse("{\"one\":{},\"two\":2, \"three\":null}");
        assertThrows(
                NullPointerException.class,
                () -> json.getObject(null)
        );
        assertNull(json.getObject("not"));
        assertEquals("{}", json.getObject("one").toString());
        // not a JsonObject
        assertThrows(
                ParseException.class,
                () -> json.getObject("two")
        );
        assertNull(json.getObject("three"));
    }

    @Test
    void objectGetArray() {
        JsonObject json = JsonObject.parse("{\"one\":[],\"two\":2, \"three\":null}");
        assertThrows(
                NullPointerException.class,
                () -> json.getArray(null)
        );
        assertNull(json.getArray("not"));
        assertEquals("[]", json.getArray("one").toString());
        // not a JsonArray
        assertThrows(
                ParseException.class,
                () -> json.getArray("two")
        );
        assertNull(json.getArray("three"));
    }

    @Test
    void objectGetString() {
        JsonObject json = JsonObject.parse("{\"one\":[],\"two\":2, \"three\":null}");
        assertThrows(
                NullPointerException.class,
                () -> json.getString(null)
        );
        assertNull(json.getString("not"));
        assertEquals("2", json.getString("two"));
        // not a String
        assertThrows(
                ParseException.class,
                () -> json.getString("one")
        );
        assertNull(json.getArray("three"));
    }

    @Test
    void objectPut() {
        JsonObject json = JsonObject.parse("{\"one\":[],\"two\":2, \"three\":null}");
        assertThrows(
                NullPointerException.class,
                () -> json.put(null, 9)
        );
        json.put("one", null);
        json.put("two", new JsonObject().put("9", 9));
        json.put("three", new JsonArray());
        json.put("four", 4);
        json.put("five", new StringBuilder("five"));
        assertEquals(
                "{\"five\":\"five\",\"four\":\"4\",\"one\":null,\"three\":[],\"two\":{\"9\":\"9\"}}",
                json.toString()
        );
    }

    @Test
    void arrayParse() {
        // unexpected symbol
        assertThrows(
                ParseException.class,
                () -> JsonObject.parse("{\"one\":[\"one\",\"two\":]}")
        );
    }

    @Test
    void arrayGetObject() {
        JsonArray json = JsonObject.parse("{\"array\":[{},\"two\",\"three\",null]}").getArray("array");
        assertEquals(4, json.length());
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getObject(-1)
        );
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getObject(4)
        );
        assertNull(json.getObject(3));
        assertEquals("{}", json.getObject(0).toString());
        // not a JsonObject
        assertThrows(
                ParseException.class,
                () -> json.getObject(1)
        );
    }

    @Test
    void arrayGetArray() {
        JsonArray json = JsonObject.parse("{\"array\":[{},[],\"three\",null]}").getArray("array");
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getArray(-1)
        );
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getArray(4)
        );
        assertNull(json.getArray(3));
        assertEquals("[]", json.getArray(1).toString());
        // not a JsonArray
        assertThrows(
                ParseException.class,
                () -> json.getObject(2)
        );
    }

    @Test
    void arrayGetString() {
        JsonArray json = JsonObject.parse("{\"array\":[{},[],\"three\",null]}").getArray("array");
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getString(-1)
        );
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getString(4)
        );
        assertNull(json.getString(3));
        assertEquals("three", json.getString(2));
        // not a String
        assertThrows(
                ParseException.class,
                () -> json.getString(1)
        );
    }

    @Test
    void arrayPut() {
        JsonArray json = JsonObject.parse("{\"array\":[\"one\"]}").getArray("array");
        json.put(null);
        json.put(new JsonObject().put("9", 9));
        json.put(new JsonArray());
        json.put(4);
        json.put(new StringBuilder("five"));
        assertEquals(
                "[\"one\",null,{\"9\":\"9\"},[],\"4\",\"five\"]",
                json.toString()
        );
    }

    @Test
    void test() {
        JsonObject json = JsonObject.parse("{   }");
        assertNull(json.getString("s"));
        assertNull(json.getObject("s"));
        assertEquals("{}", json.toString());

        json = JsonObject.parse("{\"key\"  : " + (char) 9 + (char) 10 + (char) 13 + "\"b\"}");
        json.put("moo", "json");
        assertEquals("{\"key\":\"b\",\"moo\":\"json\"}", json.toString());
        assertEquals("b", json.getString("key"));
        assertEquals("json", json.getString("moo"));

        json = JsonObject.parse("{\"key\"  :{  } ,  \"go\":null }");
        assertEquals("{\"go\":null,\"key\":{}}", json.toString());
        assertNull(json.getString("go"));
        JsonObject k = json.getObject("key");
        assertEquals("{}", k.toString());

        json = JsonObject.parse("{\"key\"  :\"\u005F\" ,  \"go\":null }");
        assertEquals("{\"go\":null,\"key\":\"_\"}", json.toString());

        json = JsonObject.parse("{\"key\"  : \"b\" ,  \"go\": \"no\" }");
        json.put("moo", "json");
        assertEquals("{\"go\":\"no\",\"key\":\"b\",\"moo\":\"json\"}", json.toString());

        json = JsonObject.parse("{\"one\":  {\"two\":{\"three\" : 3, \"four\":4},\"five\":   5" +
                "\n,\"six\":6,\"seven\":\"7\"}, \"eight\":\"8\"}");
        assertEquals("{\"eight\":\"8\",\"one\":{\"five\":\"5\",\"seven\":\"7\",\"six\":\"6\",\"two\"" +
                ":{\"four\":\"4\",\"three\":\"3\"}}}", json.toString());
        JsonObject t = json.getObject("one").getObject("two");
        assertEquals("{\"four\":\"4\",\"three\":\"3\"}", t.toString());

        json = JsonObject.parse("{\"key\"  : \"b\" ,  \"go\": [\"one\", \"two\"], \"no\": [\"fff\", \"hhh\"  ] }");
        json.put("moo", "json");
        assertEquals("{\"go\":[\"one\",\"two\"],\"key\":\"b\",\"moo\":\"json\",\"no\":[\"fff\",\"hhh\"]}", json.toString());
    }
}