// thin wrapper for the bluzelle client
// usage:
//    Wrapper wrapper = new Wrapper();
//    wrapper.connect(mnemonicString, endpointString, uuidString, chainIdString);
//    String result = wrapper.request(requestString);
// requests examples:
//    {"method":"connect","args":["mnemonic words","localhost:5000","uuid","bluzelle"]}
//    {"method":"connect","args":["mnemonic words"]}
//    {"method":"create","args":["key","value"]}
//    {method:create,args:[key,value,{gas_price:10,max_gas:10,max_fee:10}]}
//    {"method":"create","args":["key","value",{"gas_price":10},{"days":10,"hours":10,"minutes":10,"seconds":10}]}
//    {"method":"deleteAll"}
//    {"method":"delete_all","args":[{"max_gas":10000}]}
package server;

import com.bluzelle.Bluzelle;
import com.bluzelle.Connection;
import com.bluzelle.GasInfo;
import com.bluzelle.LeaseInfo;
import com.bluzelle.json.JsonArray;
import com.bluzelle.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Wrapper {
    private static final GasInfo gasInfo = new GasInfo(1000, 0, 0);
    private Bluzelle bluzelle;

    /**
     * creates and configures connection
     *
     * @param mnemonic mnemonic of the private key for account
     * @param endpoint hostname and port of rest server or null for default "http://localhost:1317"
     * @param uuid     uuid or null for the same as address
     * @param chainId  chain id of account or null for default "bluzelle"
     * @throws NullPointerException           if mnemonic == null
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public void connect(String mnemonic, String endpoint, String uuid, String chainId) {
        bluzelle = Bluzelle.connect(mnemonic, endpoint, uuid, chainId);
    }

    /**
     * @param request String with request
     * @return String result or null
     * @throws UnsupportedOperationException  if bluzelle is not connected or method is unknown
     * @throws IllegalArgumentException       if arguments are incorrect
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws Bluzelle.ServerException       if server returns error
     */
    public String request(String request) {
        JsonObject json = JsonObject.parse(request);
        String method = json.getString("method").toLowerCase();
        JsonArray args = json.getArray("args");

        if (method.equals("connect")) {
            connect(
                    args.getString(0),
                    (args.length() > 1) ? args.getString(1) : null,
                    (args.length() > 2) ? args.getString(2) : null,
                    (args.length() > 3) ? args.getString(3) : null
            );
            return null;
        } else if (bluzelle == null) {
            throw new UnsupportedOperationException("bluzelle is not connected");
        }

        switch (method) {
            case "version":
                return bluzelle.version();
            case "account":
                return bluzelle.account().toString();
            case "create":
                bluzelle.create(
                        getString(args, 0, 0),
                        getString(args, 1, 2),
                        getGasInfo(args, 2),
                        getLeaseInfo(args, 3)
                );
                return null;
            case "read":
                String value = bluzelle.read(
                        getString(args, 0, 0),
                        (args.length() > 1) && args.getBoolean(1)
                );
                if (value == null) {
                    throw new IllegalArgumentException("key not found");
                }
                return value;
            case "txread":
            case "tx_read":
                return bluzelle.txRead(getString(args, 0, 0), getGasInfo(args, 1));
            case "update":
                bluzelle.update(
                        getString(args, 0, 0),
                        getString(args, 1, 2),
                        getGasInfo(args, 2),
                        getLeaseInfo(args, 3)
                );
                return null;
            case "delete":
                bluzelle.delete(getString(args, 0, 0), getGasInfo(args, 1));
                return null;
            case "has":
                return bluzelle.has(getString(args, 0, 0)) ? "true" : "false";
            case "txhas":
            case "tx_has":
                return bluzelle.txHas(getString(args, 0, 0), getGasInfo(args, 1)) ? "true" : "false";
            case "keys":
                return listToJson(bluzelle.keys());
            case "txkeys":
            case "tx_keys":
                return listToJson(bluzelle.txKeys(getGasInfo(args, 0)));
            case "rename":
                bluzelle.rename(getString(args, 0, 0), getString(args, 1, 1), getGasInfo(args, 2));
                return null;
            case "count":
                return String.valueOf(bluzelle.count());
            case "txcount":
            case "tx_count":
                return String.valueOf(bluzelle.txCount(getGasInfo(args, 0)));
            case "deleteall":
            case "delete_all":
                bluzelle.deleteAll(getGasInfo(args, 0));
                return null;
            case "keyvalues":
            case "key_values":
                return mapToJson(bluzelle.keyValues());
            case "txkeyvalues":
            case "tx_key_values":
                return mapToJson(bluzelle.txKeyValues(getGasInfo(args, 0)));
            case "multiupdate":
            case "multi_update":
                bluzelle.multiUpdate(jsonToMap(args.getArray(0)), getGasInfo(args, 1));
                return null;
            case "getlease":
            case "get_lease":
                return String.valueOf(bluzelle.getLease(getString(args, 0, 0)));
            case "txgetlease":
            case "tx_get_lease":
                return String.valueOf(bluzelle.txGetLease(getString(args, 0, 0), getGasInfo(args, 1)));
            case "renewlease":
            case "renew_lease":
                bluzelle.renewLease(getString(args, 0, 0), getGasInfo(args, 1), getLeaseInfo(args, 2));
                return null;
            case "renewleaseall":
            case "renew_lease_all":
                bluzelle.renewLeaseAll(getGasInfo(args, 0), getLeaseInfo(args, 1));
                return null;
            case "getnshortestleases":
            case "get_n_shortest_leases":
                return mapToLeases(bluzelle.getNShortestLeases(args.getInteger(0)));
            case "txgetnshortestleases":
            case "tx_get_n_shortest_leases":
                return mapToLeases(bluzelle.txGetNShortestLeases(args.getInteger(0), getGasInfo(args, 1)));
            default:
                throw new UnsupportedOperationException("unknown method \"" + method + "\"");
        }
    }

    private String getString(JsonArray array, int index, int errorMessage) {
        try {
            return array.getString(index);
        } catch (ClassCastException e) {
            String message;
            if (errorMessage == 0) {
                message = "Key must be a string";
            } else if (errorMessage == 1) {
                message = "New key must be a string";
            } else {
                message = "Value must be a string";
            }
            throw new IllegalArgumentException(message);
        }
    }

    private static GasInfo getGasInfo(JsonArray array, int index) {
        if (array == null || index >= array.length()) {
            return gasInfo;
        }
        JsonObject json = array.getObject(index);
        Integer gasPrice = json.getInteger("gas_price");
        Integer maxGas = json.getInteger("max_gas");
        Integer maxFee = json.getInteger("max_fee");
        return new GasInfo(
                gasPrice == null ? 0 : gasPrice,
                maxGas == null ? 0 : maxGas,
                maxFee == null ? 0 : maxFee
        );
    }

    private static LeaseInfo getLeaseInfo(JsonArray array, int index) {
        if (array == null || index >= array.length()) {
            return null;
        }
        JsonObject json = array.getObject(index);
        Integer days = json.getInteger("days");
        Integer hours = json.getInteger("hours");
        Integer minutes = json.getInteger("minutes");
        Integer seconds = json.getInteger("seconds");
        return new LeaseInfo(
                days == null ? 0 : days,
                hours == null ? 0 : hours,
                minutes == null ? 0 : minutes,
                seconds == null ? 0 : seconds
        );
    }

    private static String listToJson(ArrayList<String> list) {
        JsonArray array = new JsonArray();
        for (String s : list) {
            array.put(s);
        }
        return array.toString();
    }

    private static String mapToJson(HashMap<String, String> map) {
        JsonArray array = new JsonArray();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            JsonObject object = new JsonObject();
            object.put("key", entry.getKey());
            object.put("value", entry.getValue());
            array.put(object);
        }
        return array.toString();
    }

    private static HashMap<String, String> jsonToMap(JsonArray json) {
        HashMap<String, String> map = new HashMap<>();
        JsonObject object;
        String key, value;
        int length = json.length();
        for (int i = 0; i < length; i++) {
            object = json.getObject(i);
            try {
                key = object.getString("key");
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("All keys must be strings");
            }
            try {
                value = object.getString("value");
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("All values must be strings");
            }
            map.put(key, value);
        }
        return map;
    }

    private static String mapToLeases(HashMap<String, Integer> map) {
        JsonArray array = new JsonArray();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            JsonObject object = new JsonObject();
            object.put("key", entry.getKey());
            object.put("lease", entry.getValue());
            array.put(object);
        }
        return array.toString();
    }
}