// bluzelle client
// usage:
//    Bluzelle bluzelle = Bluzelle.connect(mnemonicString, endpointString, uuidString, chainIdString);
//    String version = bluzelle.version();
//    JsonObject account = bluzelle.account();
//    bluzelle.create(keyString, valueString, gasInfo, leaseInfo);
//    String value = bluzelle.read(keyString, isProve);
//    String value = bluzelle.txRead(keyString, gasInfo);
//    bluzelle.update(keyString, valueString, gasInfo, leaseInfo);
//    bluzelle.delete(keyString, gasInfo);
//    boolean has = bluzelle.has(keyString);
//    boolean has = bluzelle.txHas(keyString, gasInfo);
//    ArrayList<String> keys = bluzelle.keys();
//    ArrayList<String> keys = bluzelle.txKeys(gasInfo);
//    bluzelle.rename(keyString, newKeyString, gasInfo);
//    int count = bluzelle.count();
//    int count = bluzelle.txCount(gasInfo);
//    bluzelle.deleteAll(gasInfo);
//    HashMap<String, String> keyValues = bluzelle.keyValues();
//    HashMap<String, String> keyValues = bluzelle.txKeyValues(gasInfo);
//    bluzelle.multiUpdate(keyValuesHashMap, gasInfo);
//    int leaseSeconds = bluzelle.getLease(keyString);
//    int leaseSeconds = bluzelle.txGetLease(keyString, gasInfo);
//    bluzelle.renewLease(keyString, gasInfo, leaseInfo);
//    bluzelle.renewLeaseAll(gasInfo, leaseInfo);
//    HashMap<String, Integer> leases = bluzelle.getNShortestLeases(n);
//    HashMap<String, Integer> leases = bluzelle.txGetNShortestLeases(n, gasInfo);
package space.aqoleg.bluzelle;

import space.aqoleg.json.JsonArray;
import space.aqoleg.json.JsonObject;
import space.aqoleg.keys.Ecc;
import space.aqoleg.keys.HdKeyPair;
import space.aqoleg.keys.Mnemonic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static space.aqoleg.bluzelle.LeaseInfo.blockTimeSeconds;
import static space.aqoleg.bluzelle.Utils.*;

public class Bluzelle {
    private final Connection connection;
    private final HdKeyPair keyPair;
    private final String address;
    private final String uuid;
    private final String chainId;
    private int accountNumber;

    private Bluzelle(Connection connection, HdKeyPair keyPair, String address, String uuid, String chainId) {
        this.connection = connection;
        this.keyPair = keyPair;
        this.address = address;
        this.uuid = uuid;
        this.chainId = chainId;
    }

    /**
     * creates and configures connection
     *
     * @param mnemonic mnemonic of the private key for account
     * @param endpoint hostname and port of rest server or null for default "http://localhost:1317"
     * @param chainId  chain id of account or null for default "bluzelle"
     * @param uuid     uuid or null for the same as address
     * @return instance of Bluzelle
     * @throws NullPointerException           if mnemonic == null
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public static Bluzelle connect(String mnemonic, String endpoint, String uuid, String chainId) {
        Connection connection = new Connection(endpoint == null ? "http://localhost:1317" : endpoint);

        HdKeyPair master = HdKeyPair.createMaster(Mnemonic.createSeed(mnemonic, "mnemonic"));
        HdKeyPair keyPair = master.generateChild("44'/118'/0'/0/0");
        String address = getAddress(keyPair);

        if (uuid == null) {
            uuid = address;
        }
        if (chainId == null) {
            chainId = "bluzelle";
        }
        Bluzelle bluzelle = new Bluzelle(connection, keyPair, address, uuid, chainId);

        JsonObject account = bluzelle.account();
        bluzelle.accountNumber = account.getInteger("account_number");

        return bluzelle;
    }

    /**
     * @return version of the service
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public String version() {
        String response = connection.get("/node_info");
        return JsonObject.parse(response).getObject("application_version").getString("version");
    }

    /**
     * @return JsonObject with information about the currently active account
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public JsonObject account() {
        String response = connection.get("/auth/accounts/" + address);
        return JsonObject.parse(response).getObject("result").getObject("value");
    }

    /**
     * create a field in the database
     *
     * @param key       name of the key to create
     * @param value     value to set the key
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException           if key == null or value == null or gasInfo == null
     * @throws IllegalArgumentException       if key contains '/'
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void create(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        } else if (key.contains("/")) {
            throw new IllegalArgumentException("Key cannot contain a slash");
        }
        if (value == null) {
            throw new NullPointerException("null value");
        }
        int blocks = 0;
        if (leaseInfo != null) {
            blocks = leaseInfo.blocks;
        }
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Value", value);
        data.put("Lease", String.valueOf(blocks));
        sendTx("/crud/create", false, data, gasInfo);
    }

    /**
     * retrieve the value of a key without consensus verification
     *
     * @param key   the key to retrieve
     * @param prove a proof of the value is required from the network
     * @return String value of the key or null
     * @throws NullPointerException           if key == null
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public String read(String key, boolean prove) {
        String path = "/crud/" + (prove ? "pread/" : "read/") + uuid + "/" + urlEncode(key);
        try {
            String response = connection.get(path);
            return JsonObject.parse(response).getObject("result").getString("value");
        } catch (Connection.ConnectionException e) {
            if (e.notFound) {
                return null;
            }
            throw e;
        }
    }

    /**
     * retrieve the value of a key via a transaction
     *
     * @param key     the key to retrieve
     * @param gasInfo object containing gas parameters
     * @return String value of the key
     * @throws NullPointerException           if key == null or gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public String txRead(String key, GasInfo gasInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        }
        JsonObject data = new JsonObject().put("Key", key);
        String response = sendTx("/crud/read", false, data, gasInfo);
        return JsonObject.parse(hexToString(response)).getString("value");
    }

    /**
     * update a field in the database
     *
     * @param key       the name of the key to create
     * @param value     value to set the key
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException           if key == null or value == null or gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void update(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        }
        if (value == null) {
            throw new NullPointerException("null value");
        }
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Value", value);
        data.put("Lease", leaseInfo == null ? "0" : String.valueOf(leaseInfo.blocks));
        sendTx("/crud/update", false, data, gasInfo);
    }

    /**
     * delete a field from the database
     *
     * @param key     the name of the key to delete
     * @param gasInfo object containing gas parameters
     * @throws NullPointerException           if key == null or gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void delete(String key, GasInfo gasInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        }
        JsonObject data = new JsonObject().put("Key", key);
        sendTx("/crud/delete", true, data, gasInfo);
    }

    /**
     * query to see if a key is in the database
     *
     * @param key the name of the key to query
     * @return value representing whether the key is in the database
     * @throws NullPointerException           if key == null
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public boolean has(String key) {
        String response = connection.get("/crud/has/" + uuid + "/" + urlEncode(key));
        return JsonObject.parse(response).getObject("result").getBoolean("has");
    }

    /**
     * query to see if a key is in the database via a transaction
     *
     * @param key     the name of the key to query
     * @param gasInfo object containing gas parameters
     * @return value representing whether the key is in the database
     * @throws NullPointerException           if key == null or gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public boolean txHas(String key, GasInfo gasInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        }
        JsonObject data = new JsonObject().put("Key", key);
        String response = sendTx("/crud/has", false, data, gasInfo);
        return JsonObject.parse(hexToString(response)).getBoolean("has");
    }

    /**
     * retrieve a list of all keys
     *
     * @return ArrayList containing all keys
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public ArrayList<String> keys() {
        String response = connection.get("/crud/keys/" + uuid);
        ArrayList<String> list = new ArrayList<>();
        JsonArray keys = JsonObject.parse(response).getObject("result").getArray("keys");
        if (keys != null) {
            int length = keys.length();
            for (int i = 0; i < length; i++) {
                list.add(keys.getString(i));
            }
        }
        return list;
    }

    /**
     * retrieve a list of all keys via a transaction
     *
     * @param gasInfo object containing gas parameters
     * @return ArrayList containing all keys
     * @throws NullPointerException           if gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public ArrayList<String> txKeys(GasInfo gasInfo) {
        String response = sendTx("/crud/keys", false, new JsonObject(), gasInfo);
        ArrayList<String> list = new ArrayList<>();
        JsonArray keys = JsonObject.parse(hexToString(response)).getArray("keys");
        if (keys != null) {
            int length = keys.length();
            for (int i = 0; i < length; i++) {
                list.add(keys.getString(i));
            }
        }
        return list;
    }

    /**
     * change the name of an existing key
     *
     * @param key     the name of the key to rename
     * @param newKey  the new name for the key
     * @param gasInfo object containing gas parameters
     * @throws NullPointerException           if key == null or newKey == null or gasInfo == null
     * @throws IllegalArgumentException       if key contains '/'
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void rename(String key, String newKey, GasInfo gasInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        }
        if (newKey == null) {
            throw new NullPointerException("null newKey");
        } else if (newKey.contains("/")) {
            throw new IllegalArgumentException("Key cannot contain a slash");
        }
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("NewKey", newKey);
        sendTx("/crud/rename", false, data, gasInfo);
    }

    /**
     * @return the number of keys in the current database/uuid
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public int count() {
        String response = connection.get("/crud/count/" + uuid);
        return Integer.parseInt(JsonObject.parse(response).getObject("result").getString("count"));
    }

    /**
     * @param gasInfo object containing gas parameters
     * @return the number of keys in the current database/uuid via a transaction
     * @throws NullPointerException           if gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public int txCount(GasInfo gasInfo) {
        String response = sendTx("/crud/count", false, new JsonObject(), gasInfo);
        return Integer.parseInt(JsonObject.parse(hexToString(response)).getString("count"));
    }

    /**
     * remove all keys in the current database/uuid
     *
     * @param gasInfo object containing gas parameters
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void deleteAll(GasInfo gasInfo) {
        sendTx("/crud/deleteall", false, new JsonObject(), gasInfo);
    }

    /**
     * enumerate all keys and values in the current database/uuid
     *
     * @return HashMap(key, value)
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public HashMap<String, String> keyValues() {
        String response = connection.get("/crud/keyvalues/" + uuid);
        JsonArray keyValues = JsonObject.parse(response).getObject("result").getArray("keyvalues");
        HashMap<String, String> map = new HashMap<>();
        JsonObject object;
        int length = keyValues.length();
        for (int i = 0; i < length; i++) {
            object = keyValues.getObject(i);
            map.put(object.getString("key"), object.getString("value"));
        }
        return map;
    }

    /**
     * enumerate all keys and values in the current database/uuid via a transaction
     *
     * @param gasInfo object containing gas parameters
     * @return HashMap(key, value)
     * @throws NullPointerException           if gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public HashMap<String, String> txKeyValues(GasInfo gasInfo) {
        String response = sendTx("/crud/keyvalues", false, new JsonObject(), gasInfo);
        JsonArray keyValues = JsonObject.parse(hexToString(response)).getArray("keyvalues");
        HashMap<String, String> map = new HashMap<>();
        JsonObject object;
        int length = keyValues.length();
        for (int i = 0; i < length; i++) {
            object = keyValues.getObject(i);
            map.put(object.getString("key"), object.getString("value"));
        }
        return map;
    }

    /**
     * update multiple fields in the database
     *
     * @param keyValues HashMap(key, value)
     * @param gasInfo   object containing gas parameters
     * @throws NullPointerException           if keyValues == null or gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void multiUpdate(HashMap<String, String> keyValues, GasInfo gasInfo) {
        JsonArray json = new JsonArray();
        JsonObject object;
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            object = new JsonObject();
            object.put("key", entry.getKey());
            object.put("value", entry.getValue());
            json.put(object);
        }
        JsonObject data = new JsonObject().put("KeyValues", json);
        sendTx("/crud/multiupdate", false, data, gasInfo);
    }

    /**
     * retrieve the minimum time remaining on the lease for a key
     *
     * @param key the key to retrieve the lease information for
     * @return minimum length of time remaining for the key's lease, in seconds
     * @throws NullPointerException           if key == null
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public int getLease(String key) {
        String response = connection.get("/crud/getlease/" + uuid + "/" + urlEncode(key));
        return Integer.parseInt(JsonObject.parse(response).getObject("result").getString("lease")) * blockTimeSeconds;
    }

    /**
     * retrieve the minimum time remaining on the lease for a key via a transaction
     *
     * @param key     the key to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return minimum length of time remaining for the key's lease, in seconds
     * @throws NullPointerException           if key == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public int txGetLease(String key, GasInfo gasInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        }
        JsonObject data = new JsonObject().put("Key", key);
        String response = sendTx("/crud/getlease", false, data, gasInfo);
        return Integer.parseInt(JsonObject.parse(hexToString(response)).getString("lease")) * blockTimeSeconds;
    }

    /**
     * update the minimum time remaining on the lease for a key
     *
     * @param key       the key to retrieve the lease information for
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException           if key == null or gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void renewLease(String key, GasInfo gasInfo, LeaseInfo leaseInfo) {
        if (key == null) {
            throw new NullPointerException("null key");
        }
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Lease", leaseInfo == null ? "0" : String.valueOf(leaseInfo.blocks));
        sendTx("/crud/renewlease", false, data, gasInfo);
    }

    /**
     * update the minimum time remaining on the lease for all keys
     *
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     * @throws NullPointerException           if gasInfo == null
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public void renewLeaseAll(GasInfo gasInfo, LeaseInfo leaseInfo) {
        JsonObject data = new JsonObject().put("Lease", leaseInfo == null ? "0" : String.valueOf(leaseInfo.blocks));
        sendTx("/crud/renewleaseall", false, data, gasInfo);
    }

    /**
     * retrieve a list of the n keys in the database with the shortest leases
     *
     * @param n the number of keys to retrieve the lease information for
     * @return HashMap(key, lease seconds)
     * @throws IllegalArgumentException       if n < 0
     * @throws Connection.ConnectionException if can not connect to the node
     */
    public HashMap<String, Integer> getNShortestLeases(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid value specified");
        }
        String response = connection.get("/crud/getnshortestleases/" + uuid + "/" + n);
        JsonArray json = JsonObject.parse(response).getObject("result").getArray("keyleases");
        int length = json.length();
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            JsonObject object = json.getObject(i);
            map.put(object.getString("key"), Integer.parseInt(object.getString("lease")) * blockTimeSeconds);
        }
        return map;
    }

    /**
     * retrieve a list of the n keys in the database with the shortest leases via a transaction
     *
     * @param n       the number of keys to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return HashMap(key, lease seconds)
     * @throws NullPointerException           if gasInfo == null
     * @throws IllegalArgumentException       if n < 0
     * @throws Connection.ConnectionException if can not connect to the node
     * @throws ServerException                if server returns error
     */
    public HashMap<String, Integer> txGetNShortestLeases(int n, GasInfo gasInfo) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid value specified");
        }
        JsonObject data = new JsonObject().put("N", String.valueOf(n));
        String response = sendTx("/crud/getnshortestleases", false, data, gasInfo);
        JsonArray json = JsonObject.parse(hexToString(response)).getArray("keyleases");
        int length = json.length();
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            JsonObject object = json.getObject(i);
            map.put(object.getString("key"), Integer.parseInt(object.getString("lease")) * blockTimeSeconds);
        }
        return map;
    }

    private String sendTx(String path, boolean delete, JsonObject data, GasInfo gasInfo) {
        data.put("BaseReq", new JsonObject().put("from", address).put("chain_id", chainId));
        data.put("UUID", uuid);
        data.put("Owner", address);

        String response = connection.post(path, delete, data);

        data = JsonObject.parse(response).getObject("value");
        JsonObject fee = setFee(data.getObject("fee"), gasInfo);

        String memo = randomString();
        JsonObject signature = sign(data.getArray("msg"), fee, memo);

        data.put("memo", memo);
        data.put("signatures", new JsonArray().put(signature));
        // data.put("signature", signature);

        JsonObject out = new JsonObject();
        out.put("tx", data);
        out.put("mode", "block");

        response = connection.post("/txs", false, out);

        data = JsonObject.parse(response);
        if (data.getInteger("code") != null) {
            throw new ServerException(extractMessage(data));
        }

        return data.getString("data");
    }

    private JsonObject setFee(JsonObject fee, GasInfo gasInfo) {
        if (gasInfo.maxGas > 0) {
            int feeMaxGas = Integer.parseInt(fee.getString("gas"));
            if (feeMaxGas > gasInfo.maxGas) {
                fee.put("gas", String.valueOf(gasInfo.maxGas));
            }
        }

        int amount = gasInfo.maxFee;
        if (amount == 0) {
            int gas = Integer.parseInt(fee.getString("gas"));
            amount = gas * gasInfo.gasPrice;
        }

        JsonObject feeAmount = new JsonObject();
        feeAmount.put("denom", "ubnt");
        feeAmount.put("amount", String.valueOf(amount));
        fee.put("amount", new JsonArray().put(feeAmount));

        return fee;
    }

    private JsonObject sign(JsonArray message, JsonObject fee, String memo) {
        int sequence = account().getInteger("sequence");

        JsonObject payload = new JsonObject();
        payload.put("account_number", String.valueOf(accountNumber));
        payload.put("chain_id", chainId);
        payload.put("fee", fee);
        payload.put("memo", memo);
        payload.put("msgs", message);
        payload.put("sequence", String.valueOf(sequence));

        byte[] hash = sha256hash(payload.toSanitizeString().getBytes());
        byte[] signature = Ecc.ecc.sign(hash, keyPair.d);

        JsonObject out = new JsonObject();
        JsonObject publicKey = new JsonObject();
        publicKey.put("type", "tendermint/PubKeySecp256k1");
        publicKey.put("value", base64encode(keyPair.publicKeyToByteArray()));
        out.put("pub_key", publicKey);
        out.put("signature", base64encode(signature));
        out.put("account_number", String.valueOf(accountNumber));
        out.put("sequence", String.valueOf(sequence));

        return out;
    }

    private String extractMessage(JsonObject data) {
        String log = data.getString("raw_log");
        if (log == null) {
            return null;
        }
        int startPos = log.indexOf(": ");
        if (startPos < 0) {
            return log;
        }
        // "insufficient fee: insufficient fees; got: 10ubnt required: 2000000ubnt"
        if (log.substring(0, startPos).equals("insufficient fee")) {
            return log.substring(startPos + 2);
        }
        // "unauthorized: Key already exists: failed to execute message; message index: 0"
        int endPos = log.indexOf(":", startPos + 1);
        if (endPos < 0) {
            return log.substring(startPos + 2);
        }
        return log.substring(startPos + 2, log.indexOf(":", startPos + 1));
    }

    public class ServerException extends RuntimeException {
        private ServerException(String message) {
            super(message);
        }
    }
}