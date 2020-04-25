package space.aqoleg.bluzelle;

import space.aqoleg.crypto.Ecc;
import space.aqoleg.crypto.Sha256;
import space.aqoleg.exception.ConnectionException;
import space.aqoleg.exception.EndpointException;
import space.aqoleg.exception.NullException;
import space.aqoleg.exception.ResponseException;
import space.aqoleg.json.JsonArray;
import space.aqoleg.json.JsonObject;
import space.aqoleg.keys.HdKeyPair;
import space.aqoleg.keys.KeyPair;
import space.aqoleg.keys.Mnemonic;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static space.aqoleg.bluzelle.Utils.*;
import static space.aqoleg.utils.Converter.hexToString;

public class Bluzelle {
    static final int blockTimeSeconds = 5;
    private final KeyPair keyPair;
    private final String address;
    private final String endpoint;
    private final String uuid;
    private final String chainId;
    private int accountNumber;

    private Bluzelle(KeyPair keyPair, String address, String endpoint, String uuid, String chainId) {
        this.keyPair = keyPair;
        this.address = address;
        this.endpoint = endpoint == null ? "http://localhost:1317" : endpoint;
        this.uuid = uuid == null ? address : uuid;
        this.chainId = chainId == null ? "bluzelle" : chainId;
    }

    /**
     * configures the Bluzelle connection
     *
     * @param address  address of account
     * @param mnemonic mnemonic of the private key for account
     * @param endpoint hostname and port of rest server or null
     * @param uuid     uuid or null
     * @param chainId  chain id of account or null
     * @return instance of Bluzelle
     */
    public static Bluzelle getInstance(
            String address,
            String mnemonic,
            String endpoint,
            String uuid,
            String chainId
    ) {
        HdKeyPair master = HdKeyPair.createMaster(Mnemonic.createSeed(mnemonic, "mnemonic"));
        KeyPair keyPair = master.generateChild("44'/118'/0'/0/0").keyPair;
        Bluzelle bluzelle = new Bluzelle(keyPair, address, endpoint, uuid, chainId);
        JsonObject account = bluzelle.account();
        bluzelle.accountNumber = Integer.parseInt(account.getString("account_number"));
        return bluzelle;
    }

    /**
     * @return version of the service
     */
    public String version() {
        String response = httpGet("/node_info");
        return JsonObject.parse(response).getObject("application_version").getString("version");
    }

    /**
     * @return JsonObject with information about the currently active account
     */
    public JsonObject account() {
        String response = httpGet("/auth/accounts/" + address);
        return JsonObject.parse(response).getObject("result").getObject("value");
    }

    /**
     * create a field in the database
     *
     * @param key       name of the key to create
     * @param value     value to set the key
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     */
    public void create(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Value", value);
        data.put("Lease", leaseInfo == null ? 0 : leaseInfo.blocks);
        sendTx("/crud/create", data, gasInfo);
    }

    /**
     * retrieve the value of a key without consensus verification
     *
     * @param key   the key to retrieve
     * @param prove a proof of the value is required from the network
     * @return String value of the key or null
     */
    public String read(String key, boolean prove) {
        String path = "/crud/" + (prove ? "pread/" : "read/") + uuid + "/" + encode(key);
        try {
            String response = httpGet(path);
            return JsonObject.parse(response).getObject("result").getString("value");
        } catch (NullException ignored) {
            return null;
        }
    }

    /**
     * retrieve the value of a key via a transaction
     *
     * @param key     the key to retrieve
     * @param gasInfo object containing gas parameters
     * @return String value of the key
     */
    public String txRead(String key, GasInfo gasInfo) {
        JsonObject data = new JsonObject().put("Key", key);
        String response = sendTx("/crud/read", data, gasInfo);
        return JsonObject.parse(hexToString(response)).getString("value");
    }

    /**
     * update a field in the database
     *
     * @param key       the name of the key to create
     * @param value     value to set the key
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     */
    public void update(String key, String value, GasInfo gasInfo, LeaseInfo leaseInfo) {
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Value", value);
        data.put("Lease", leaseInfo == null ? 0 : leaseInfo.blocks);
        sendTx("/crud/update", data, gasInfo);
    }

    /**
     * delete a field from the database
     *
     * @param key     the name of the key to delete
     * @param gasInfo object containing gas parameters
     */
    public void delete(String key, GasInfo gasInfo) {
        JsonObject data = new JsonObject().put("Key", key);
        sendTx("/crud/delete", data, gasInfo);
    }

    /**
     * query to see if a key is in the database
     *
     * @param key the name of the key to query
     * @return value representing whether the key is in the database
     */
    public boolean has(String key) {
        String response = httpGet("/crud/has/" + uuid + "/" + encode(key));
        return JsonObject.parse(response).getObject("result").getString("has").equals("true");
    }

    /**
     * query to see if a key is in the database via a transaction
     *
     * @param key     the name of the key to query
     * @param gasInfo object containing gas parameters
     * @return value representing whether the key is in the database
     */
    public boolean txHas(String key, GasInfo gasInfo) {
        JsonObject data = new JsonObject().put("Key", key);
        String response = sendTx("/crud/has", data, gasInfo);
        return JsonObject.parse(hexToString(response)).getString("has").equals("true");
    }

    /**
     * retrieve a list of all keys
     *
     * @return ArrayList containing all keys
     */
    public ArrayList<String> keys() {
        String response = httpGet("/crud/keys/" + uuid);
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
     */
    public ArrayList<String> txKeys(GasInfo gasInfo) {
        String response = sendTx("/crud/keys", new JsonObject(), gasInfo);
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
     */
    public void rename(String key, String newKey, GasInfo gasInfo) {
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("NewKey", newKey);
        sendTx("/crud/rename", data, gasInfo);
    }

    /**
     * @return the number of keys in the current database/uuid
     */
    public int count() {
        String response = httpGet("/crud/count/" + uuid);
        return parse(JsonObject.parse(response).getObject("result").getString("count"));
    }

    /**
     * @param gasInfo object containing gas parameters
     * @return the number of keys in the current database/uuid via a transaction
     */
    public int txCount(GasInfo gasInfo) {
        String response = sendTx("/crud/count", new JsonObject(), gasInfo);
        return parse(JsonObject.parse(hexToString(response)).getString("count"));
    }

    /**
     * remove all keys in the current database/uuid
     *
     * @param gasInfo object containing gas parameters
     */
    public void deleteAll(GasInfo gasInfo) {
        sendTx("/crud/deleteall", new JsonObject(), gasInfo);
    }

    /**
     * enumerate all keys and values in the current database/uuid
     *
     * @return HashMap(key, value)
     */
    public HashMap<String, String> keyValues() {
        String response = httpGet("/crud/keyvalues/" + uuid);
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
     */
    public HashMap<String, String> txKeyValues(GasInfo gasInfo) {
        String response = sendTx("/crud/keyvalues", new JsonObject(), gasInfo);
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
        sendTx("/crud/multiupdate", data, gasInfo);
    }

    /**
     * retrieve the minimum time remaining on the lease for a key
     *
     * @param key the key to retrieve the lease information for
     * @return minimum length of time remaining for the key's lease, in seconds
     */
    public int getLease(String key) {
        String response = httpGet("/crud/getlease/" + uuid + "/" + encode(key));
        return parse(JsonObject.parse(response).getObject("result").getString("lease")) * blockTimeSeconds;
    }

    /**
     * retrieve the minimum time remaining on the lease for a key via a transaction
     *
     * @param key     the key to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return minimum length of time remaining for the key's lease, in seconds
     */
    public int txGetLease(String key, GasInfo gasInfo) {
        JsonObject data = new JsonObject().put("Key", key);
        String response = sendTx("/crud/getlease", data, gasInfo);
        return parse(JsonObject.parse(hexToString(response)).getString("lease")) * blockTimeSeconds;
    }

    /**
     * update the minimum time remaining on the lease for a key
     *
     * @param key       the key to retrieve the lease information for
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     */
    public void renewLease(String key, GasInfo gasInfo, LeaseInfo leaseInfo) {
        JsonObject data = new JsonObject();
        data.put("Key", key);
        data.put("Lease", leaseInfo == null ? 0 : leaseInfo.blocks);
        sendTx("/crud/renewlease", data, gasInfo);
    }

    /**
     * update the minimum time remaining on the lease for all keys
     *
     * @param gasInfo   object containing gas parameters
     * @param leaseInfo minimum time for key to remain in database or null
     */
    public void renewLeaseAll(GasInfo gasInfo, LeaseInfo leaseInfo) {
        JsonObject data = new JsonObject().put("Lease", leaseInfo == null ? 0 : leaseInfo.blocks);
        sendTx("/crud/renewleaseall", data, gasInfo);
    }

    /**
     * retrieve a list of the n keys in the database with the shortest leases
     *
     * @param n the number of keys to retrieve the lease information for
     * @return Map(key, lease seconds)
     */
    public Map<String, Integer> getNShortestLeases(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("negative n");
        }
        String response = httpGet("/crud/getnshortestleases/" + uuid + "/" + n);
        JsonArray json = JsonObject.parse(response).getObject("result").getArray("keyleases");
        int length = json.length();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            JsonObject object = json.getObject(i);
            map.put(object.getString("key"), parse(object.getString("lease")) * blockTimeSeconds);
        }
        return map;
    }

    /**
     * retrieve a list of the n keys in the database with the shortest leases via a transaction
     *
     * @param n       the number of keys to retrieve the lease information for
     * @param gasInfo object containing gas parameters
     * @return Map(key, lease seconds)
     */
    public Map<String, Integer> txGetNShortestLeases(int n, GasInfo gasInfo) {
        if (n < 0) {
            throw new IllegalArgumentException("negative n");
        }
        JsonObject data = new JsonObject().put("N", String.valueOf(n));
        String response = sendTx("/crud/getnshortestleases", data, gasInfo);
        JsonArray json = JsonObject.parse(hexToString(response)).getArray("keyleases");
        int length = json.length();
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < length; i++) {
            JsonObject object = json.getObject(i);
            map.put(object.getString("key"), parse(object.getString("lease")) * blockTimeSeconds);
        }
        return map;
    }

    private String httpGet(String path) {
        try {
            URL url = new URL(endpoint + path);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuilder builder = new StringBuilder();
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    return builder.toString();
                }
                builder.append(input);
            } while (true);
        } catch (MalformedURLException e) {
            throw new EndpointException(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new NullException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    private String httpPost(String path, JsonObject data) {
        try {
            URL url = new URL(endpoint + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            if (path.equals("/crud/delete")) {
                connection.setRequestMethod("DELETE");
            } else {
                connection.setRequestMethod("POST");
            }
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            OutputStream stream = connection.getOutputStream();
            stream.write(data.toString().getBytes("utf-8"));
            stream.flush();
            stream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuilder builder = new StringBuilder();
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    return builder.toString();
                }
                builder.append(input);
            } while (true);
        } catch (MalformedURLException e) {
            throw new EndpointException(e.getMessage());
        } catch (FileNotFoundException e) {
            throw new NullException(e.getMessage());
        } catch (IOException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    private String sendTx(String path, JsonObject data, GasInfo gasInfo) {
        JsonObject baseReq = new JsonObject();
        baseReq.put("from", address);
        baseReq.put("chain_id", chainId);
        data.put("BaseReq", baseReq);
        data.put("UUID", uuid);
        data.put("Owner", address);

        String response = httpPost(path, data);
        data = JsonObject.parse(response).getObject("value");
        JsonObject fee = data.getObject("fee");
        if (gasInfo.maxGas > 0 && parse(fee.getString("gas")) > gasInfo.maxGas) {
            fee.put("gas", gasInfo.maxGas);
        }
        int amount = gasInfo.maxFee;
        if (amount == 0) {
            amount = parse(fee.getString("gas")) * gasInfo.gasPrice;
        }
        JsonObject feeAmount = new JsonObject();
        feeAmount.put("denom", "ubnt");
        feeAmount.put("amount", amount);
        fee.put("amount", new JsonArray().put(feeAmount));

        String memo = makeRandomString();
        JsonObject signature = sign(data.getArray("msg"), fee, memo);
        data.put("memo", memo);
        data.put("signatures", new JsonArray().put(signature));
        data.put("signature", signature);
        JsonObject out = new JsonObject();
        out.put("tx", data);
        out.put("mode", "block");

        response = httpPost("/txs", out);
        data = JsonObject.parse(response);
        if (data.getString("code") != null) {
            throw new ResponseException(data.getString("raw_log"));
        }
        return data.getString("data");
    }

    private JsonObject sign(JsonArray msg, JsonObject fee, String memo) {
        String sequence = account().getString("sequence");

        JsonObject payload = new JsonObject();
        payload.put("account_number", accountNumber);
        payload.put("chain_id", chainId);
        payload.put("fee", fee);
        payload.put("memo", memo);
        payload.put("msgs", msg);
        payload.put("sequence", sequence);

        byte[] hash = Sha256.getHash(payload.toString().getBytes());
        BigInteger[] signature = Ecc.secp256k1.sign(hash, keyPair.d);
        byte[] signatureArray = new byte[64];
        byte[] r = signature[0].toByteArray();
        int rStart = r[0] == 0 ? 1 : 0;
        System.arraycopy(r, rStart, signatureArray, 32 - (r.length - rStart), r.length - rStart);
        byte[] s = signature[1].toByteArray();
        int sStart = s[0] == 0 ? 1 : 0;
        System.arraycopy(s, sStart, signatureArray, 64 - (s.length - sStart), s.length - sStart);

        JsonObject out = new JsonObject();
        JsonObject publicKey = new JsonObject();
        publicKey.put("type", "tendermint/PubKeySecp256k1");
        publicKey.put("value", Base64.getEncoder().encodeToString(keyPair.publicKey.toByteArray()));
        out.put("pub_key", publicKey);
        out.put("signature", Base64.getEncoder().encodeToString(signatureArray));
        out.put("account_number", accountNumber);
        out.put("sequence", sequence);
        return out;
    }
}