package space.aqoleg.server;

import space.aqoleg.bluzelle.Bluzelle;
import space.aqoleg.bluzelle.Connection;
import space.aqoleg.bluzelle.GasInfo;
import space.aqoleg.bluzelle.LeaseInfo;
import space.aqoleg.json.JsonArray;
import space.aqoleg.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Wrapper {
    private static final GasInfo gasInfo = new GasInfo(1000, 0, 0);
    private static Bluzelle bluzelle;

    private Wrapper() {
    }

    /**
     * @param request String with request
     *                for example {"method":"create","args":["myKey","myValue",{"gas_price":10},{"days":10}]}
     * @return result as a String or as a json string or String with error message
     */
    public static String wrap(String request) {
        if (bluzelle == null) {
            initialize();
        }
        try {
            return proceed(request);
        } catch (Connection.ConnectionException | Bluzelle.ServerException e) {
            return e.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private static void initialize() {
        String mnemonic = System.getenv("MNEMONIC");
        if (mnemonic == null) {
            mnemonic = "around buzz diagram captain obtain detail salon mango muffin brother morning jeans" +
                    " display attend knife carry green dwarf vendor hungry fan route pumpkin car";
        }
        String endpoint = System.getenv("ENDPOINT");
        if (endpoint == null) {
            endpoint = "http://testnet.public.bluzelle.com:1317";
        }
        String uuid = System.getenv("UUID");
        String chainId = System.getenv("CHAIN_ID");
        bluzelle = Bluzelle.connect(mnemonic, endpoint, uuid, chainId);
    }

    private static String proceed(String request) {
        JsonObject json = JsonObject.parse(request);
        JsonArray args = json.getArray("args");
        switch (json.getString("method")) {
            case "version":
                return bluzelle.version();
            case "account":
                return bluzelle.account().toString();
            case "create":
                bluzelle.create(
                        args.getString(0),
                        args.getString(1),
                        getGasInfo(args, 2),
                        getLeaseInfo(args, 3)
                );
                return "ok";
            case "read":
                boolean prove = false;
                if (args.length() > 1) {
                    prove = args.getString(1).equals("true");
                }
                return bluzelle.read(args.getString(0), prove);
            case "txRead":
            case "tx_read":
                return bluzelle.txRead(args.getString(0), getGasInfo(args, 1));
            case "update":
                bluzelle.update(
                        args.getString(0),
                        args.getString(1),
                        getGasInfo(args, 2),
                        getLeaseInfo(args, 3)
                );
                return "ok";
            case "delete":
                bluzelle.delete(args.getString(0), getGasInfo(args, 1));
                return "ok";
            case "has":
                return bluzelle.has(args.getString(0)) ? "true" : "false";
            case "txHas":
            case "tx_has":
                return bluzelle.txHas(args.getString(0), getGasInfo(args, 1)) ? "true" : "false";
            case "keys":
                return listToJson(bluzelle.keys());
            case "txKeys":
            case "tx_keys":
                return listToJson(bluzelle.txKeys(getGasInfo(args, 0)));
            case "rename":
                bluzelle.rename(args.getString(0), args.getString(1), getGasInfo(args, 2));
                return "ok";
            case "count":
                return String.valueOf(bluzelle.count());
            case "txCount":
            case "tx_count":
                return String.valueOf(bluzelle.txCount(getGasInfo(args, 0)));
            case "deleteAll":
            case "delete_all":
                bluzelle.deleteAll(getGasInfo(args, 0));
                return "ok";
            case "keyValues":
            case "key_values":
                return mapToJson(bluzelle.keyValues());
            case "txKeyValues":
            case "tx_key_values":
                return mapToJson(bluzelle.txKeyValues(getGasInfo(args, 0)));
            case "multiUpdate":
            case "multi_update":
                bluzelle.multiUpdate(jsonToMap(args.getArray(0)), getGasInfo(args, 1));
                return "ok";
            case "getLease":
            case "get_lease":
                return String.valueOf(bluzelle.getLease(args.getString(0)));
            case "txGetLease":
            case "tx_get_lease":
                return String.valueOf(bluzelle.txGetLease(args.getString(0), getGasInfo(args, 1)));
            case "renewLease":
            case "renew_lease":
                bluzelle.renewLease(args.getString(0), getGasInfo(args, 1), getLeaseInfo(args, 2));
                return "ok";
            case "renewLeaseAll":
            case "renew_lease_all":
                bluzelle.renewLeaseAll(getGasInfo(args, 0), getLeaseInfo(args, 1));
                return "ok";
            case "getNShortestLeases":
            case "get_n_shortest_leases":
                return mapToLeases(bluzelle.getNShortestLeases(args.getInt(0)));
            case "txGetNShortestLeases":
            case "tx_get_n_shortest_leases":
                return mapToLeases(bluzelle.txGetNShortestLeases(args.getInt(0), getGasInfo(args, 1)));
            default:
                throw new IllegalArgumentException("method is unaccepted");
        }
    }

    private static GasInfo getGasInfo(JsonArray array, int index) {
        if (array == null || index >= array.length()) {
            return gasInfo;
        }
        JsonObject json = array.getObject(index);
        int gasPrice = 0;
        int maxGas = 0;
        int maxFee = 0;
        try {
            gasPrice = json.getInt("gas_price");
        } catch (Exception ignored) {
        }
        try {
            maxGas = json.getInt("max_gas");
        } catch (Exception ignored) {
        }
        try {
            maxFee = json.getInt("max_fee");
        } catch (Exception ignored) {
        }
        return new GasInfo(gasPrice, maxGas, maxFee);
    }

    private static LeaseInfo getLeaseInfo(JsonArray array, int index) {
        if (array == null || index >= array.length()) {
            return null;
        }
        JsonObject json = array.getObject(index);
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        try {
            days = json.getInt("days");
        } catch (Exception ignored) {
        }
        try {
            hours = json.getInt("hours");
        } catch (Exception ignored) {
        }
        try {
            minutes = json.getInt("minutes");
        } catch (Exception ignored) {
        }
        try {
            seconds = json.getInt("seconds");
        } catch (Exception ignored) {
        }
        return new LeaseInfo(days, hours, minutes, seconds);
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
        map.forEach((key, value) -> {
            JsonObject object = new JsonObject();
            object.put("key", key);
            object.put("value", value);
            array.put(object);
        });
        return array.toString();
    }

    private static HashMap<String, String> jsonToMap(JsonArray json) {
        HashMap<String, String> map = new HashMap<>();
        JsonObject object;
        int length = json.length();
        for (int i = 0; i < length; i++) {
            object = json.getObject(i);
            map.put(object.getString("key"), object.getString("value"));
        }
        return map;
    }

    private static String mapToLeases(Map<String, Integer> map) {
        JsonArray array = new JsonArray();
        map.forEach((key, value) -> {
            JsonObject object = new JsonObject();
            object.put("key", key);
            object.put("lease", value);
            array.put(object);
        });
        return array.toString();
    }
}