package space.aqoleg.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

    @Test
    void nextCharTest() {
        JsonObject json = JsonObject.parse("{\"one\":  \n \r \t    \"1\"}");
        assertEquals("{\"one\":\"1\"}", json.toString());
        // unclosed
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("")
        );
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{")
        );
    }

    @Test
    void nextKeyTest() {
        // empty key
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{\"\":1")
        );
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{:1")
        );
        JsonObject json = JsonObject.parse("{\"one\":one, 'two'  :two, three   :3}");
        assertEquals("{\"one\":\"one\",\"three\":\"3\",\"two\":\"two\"}", json.toString());
    }

    @Test
    void nextValueTest() {
        JsonObject json = JsonObject.parse("{\"one\":null,\"two\": true in ,\"three\":-90.89 ,\"four\": [],  x:  , }");
        assertEquals("{\"four\":[],\"one\":null,\"three\":\"-90.89\",\"two\":\"true in\",\"x\":\"\"}", json.toString());
    }

    @Test
    void parseStringTest() {
        // unclosed
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{\"one\":\"t")
        );
        // incorrect char u
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{\"one\":\"r\\uuuuu\"")
        );
        JsonObject json = JsonObject.parse("{one:\' \\\', \\\", \\\\, \\/ \'}");
        assertEquals(" \', \", \\, / ", json.getString("one"));
        assertEquals("{\"one\":\" \\\', \\\", \\\\, / \"}", json.toString());

        json = JsonObject.parse("{\"one\":\" \\b, \\f, \\n, \\r, \\t \"}");
        assertEquals(" \b, \f, \n, \r, \t ", json.getString("one"));
        assertEquals("{\"one\":\" \\b, \\f, \\n, \\r, \\t \"}", json.toString());

        json = JsonObject.parse("{\"one\":\"\\u0048\"}");
        assertEquals("{\"one\":\"H\"}", json.toString());

        json = new JsonObject();
        json.put("one", ">,  \u003C <  &&");
        assertEquals(">,  < <  &&", json.getString("one"));
        assertEquals("{\"one\":\">,  < <  &&\"}", json.toString());
        assertEquals("{\"one\":\"\\u003E,  \\u003C \\u003C  \\u0026\\u0026\"}", json.toSanitizeString());

        json = JsonObject.parse(json.toString());
        assertEquals(">,  < <  &&", json.getString("one"));
    }

    @Test
    void parseUnquotedStringTest() {
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{one")
        );
        JsonObject json = JsonObject.parse("{one  1 : 1 1, t wo  : tttt t t  5, i :[ikk k, o m  ,], }");
        assertEquals("{\"i\":[\"ikk k\",\"o m\"],\"one  1\":\"1 1\",\"t wo\":\"tttt t t  5\"}", json.toString());
    }

    @Test
    void objectParse() {
        assertThrows(
                NullPointerException.class,
                () -> JsonObject.parse((String) null)
        );
        // incorrect first char
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("r")
        );
        // duplicate key
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{\"one\":1,\"one\":1}")
        );
        // no ':' after key
        assertThrows(
                JsonException.class,
                () -> JsonObject.parse("{\"one\"1}")
        );
        // unexpected symbol
        assertThrows(
                JsonException.class,
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
                JsonException.class,
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
                JsonException.class,
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
                JsonException.class,
                () -> json.getString("one")
        );
        assertNull(json.getArray("three"));
    }

    @Test
    void objectGetInt() {
        JsonObject json = JsonObject.parse("{\"one\":[],\"two\":S2, \"three\":'3'}");
        assertThrows(
                NullPointerException.class,
                () -> json.getInt(null)
        );
        assertThrows(
                JsonException.class,
                () -> json.getInt("no")
        );
        assertThrows(
                JsonException.class,
                () -> json.getInt("one")
        );
        assertThrows(
                JsonException.class,
                () -> json.getInt("two")
        );
        assertEquals(3, json.getInt("three"));
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
        json.put("three", null);
        json.put("four", 4);
        json.put("five", new StringBuilder("five"));
        assertEquals(
                "{\"five\":\"five\",\"four\":\"4\",\"two\":{\"9\":\"9\"}}",
                json.toString()
        );
    }

    @Test
    void arrayParse() {
        // unexpected symbol
        assertThrows(
                JsonException.class,
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
                JsonException.class,
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
                JsonException.class,
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
                JsonException.class,
                () -> json.getString(1)
        );
    }

    @Test
    void arrayGetInt() {
        JsonArray json = JsonObject.parse("{array:[{},S2,3 ]}").getArray("array");
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getInt(-1)
        );
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> json.getInt(4)
        );
        assertThrows(
                JsonException.class,
                () -> json.getInt(0)
        );
        assertThrows(
                JsonException.class,
                () -> json.getInt(1)
        );
        assertEquals(3, json.getInt(2));
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
    void escapeCharTest() {
        JsonObject json = JsonObject.parse("{one:\" \\\\ \\\' \\\" \\u0079 \\u0aaa \\u0555 \"}");
        assertEquals(" \\ \' \" y \u0aaa \u0555 ", json.getString("one"));
        assertEquals("{\"one\":\" \\\\ \\\' \\\" \u0079 \u0aaa \u0555 \"}", json.toString());
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

        json = JsonObject.parse("{\"key\"  : \"b\" ,  \"go\": [\"o\", \"t\"], \"no\": [\"fff\", \"h\"  ] }");
        json.put("moo", "json");
        assertEquals("{\"go\":[\"o\",\"t\"],\"key\":\"b\",\"moo\":\"json\",\"no\":[\"fff\",\"h\"]}", json.toString());

        String input = "{big: BB b , a : [], 'cool':12 , x:{array:\"a\",g:[], 'empty' : { \n }, } , }";
        json = JsonObject.parse(input);
        assertEquals("BB b", json.getString("big"));
        assertEquals(0, json.getArray("a").length());
        assertEquals("12", json.getString("cool"));
        JsonObject x = json.getObject("x");
        assertEquals("a", x.getString("array"));
        assertEquals("{}", x.getObject("empty").toString());
        x.getArray("g").put("54a");
        x.getArray("g").put("t");
        assertEquals(2, x.getArray("g").length());
        String output = "{\"a\":[],\"big\":\"BB b\",\"cool\":\"12\",\"x\":{\"array\":\"a\",\"empty\":{}," +
                "\"g\":[\"54a\",\"t\"]}}";
        assertEquals(output, json.toString());

        json = JsonObject.parse("{key : , go:[go, null, 5-3  , { h: <>}, ]  }");
        assertEquals("{\"go\":[\"go\",null,\"5-3\",{\"h\":\"\\u003C\\u003E\"}],\"key\":\"\"}", json.toSanitizeString());
    }
}