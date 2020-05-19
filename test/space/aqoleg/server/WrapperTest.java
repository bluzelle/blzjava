package space.aqoleg.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WrapperTest {

    @Test
    void test() {
        String request = "{";
        System.out.println(Wrapper.wrap(request));

        request = "{method:no}";
        System.out.println(Wrapper.wrap(request));

        request = "{method:version}";
        System.out.println(Wrapper.wrap(request));

        request = "{method:deleteAll,args:[]}";
        assertEquals("ok", Wrapper.wrap(request));

        request = "{method:account,args:[]}";
        System.out.println(Wrapper.wrap(request));

        request = "{method:create,args:[key,value]}";
        assertEquals("ok", Wrapper.wrap(request));

        request = "{method:create,args:[key,value]}";
        System.out.println(Wrapper.wrap(request));

        request = "{method:create,args:[key1,value,{max_fee:10}]}";
        System.out.println(Wrapper.wrap(request));

        request = "{method:read,args:[key]}";
        assertEquals("value", Wrapper.wrap(request));

        request = "{method:update,args:[key,new value,{gas_price:1000}]}";
        assertEquals("ok", Wrapper.wrap(request));

        request = "{method:has,args:[nokey]}";
        assertEquals("false", Wrapper.wrap(request));

        request = "{method:tx_has,args:[key]}";
        assertEquals("true", Wrapper.wrap(request));

        request = "{method:rename,args:[key,key2]}";
        assertEquals("ok", Wrapper.wrap(request));

        request = "{method:create,args:[key1,value,{gas_price:1000},{minutes:1}]}";
        assertEquals("ok", Wrapper.wrap(request));

        request = "{method:multiUpdate,args:[[{key:key1,value:value11},{key:key2,value:value22}]]}";
        assertEquals("ok", Wrapper.wrap(request));

        request = "{method:txKeyValues,args:[]}";
        System.out.println(Wrapper.wrap(request));

        request = "{method:getNShortestLeases,args:[2]}";
        System.out.println(Wrapper.wrap(request));

        request = "{method:deleteAll,args:[]}";
        assertEquals("ok", Wrapper.wrap(request));
    }
}