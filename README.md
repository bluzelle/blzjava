<a href="https://bluzelle.com/"><img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/></a>

# Getting started

Ensure you have java 8 or heigher.

Get the package from github:
```
git clone https://github.com/aqoleg/blzjava
```
Or [download](https://github.com/aqoleg/blzjava/releases/download/1.0.0/blzjava.jar) jar file and add classpath.

Use:
```java
import space.aqoleg.bluzelle.*;

public class Main {
    public static void main(String[] args) {
        Bluzelle bluzelle = Bluzelle.getInstance(
            "address",
            "mnemonic",
            "endpoint",
            "uuid",
            "chainId"
        );

        GasInfo gasInfo = new GasInfo(0, 0, 300);
        LeaseInfo leaseInfo = new LeaseInfo(1, 0, 0, 0);

        bluzelle.create("key", "value", gasInfo, leaseInfo);
        bluzelle.update("key", "new value", gasInfo, leaseInfo);
        System.out.println(bluzelle.read("key", false));
        bluzelle.delete("key", gasInfo);
        System.out.println(bluzelle.has("key"));
    }
}
```

You can find javadocs for all public functions and some [examples](https://github.com/aqoleg/blzjava/tree/master/samples).

# API documentation


### bluzelle\(address, mnemonic, uuid, endpoint, chainId\)

Configures the Bluzelle connection. This may be called multiple times to create multiple clients.

```
Bluzelle bluzelle = Bluzelle.getInstance(
    "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
    "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
    "http://testnet.public.bluzelle.com:1317",
    null,
    null
);
```

| Argument | Description |
| :--- | :--- |
| **address** | The address of your Bluzelle account |
| **mnemonic** | The mnemonic of the private key for your Bluzelle account |
| **endpoint** | The hostname and port of your rest server. Default: http://localhost:1317 |
| **uuid** | Bluzelle uses `UUID`'s to identify distinct databases on a single swarm. We recommend using [Version 4 of the universally unique identifier](https://en.wikipedia.org/wiki/Universally_unique_identifier#Version_4_%28random%29). Defaults to the account address. |
| **chainId** | The chain id of your Bluzelle account. Default: bluzelle |


### version\()

Retrieve the version of the Bluzelle service.

Returns string containing the version information, e.g.

```
0.0.0-39-g8895e3e
```

Throws an exception if a response is not received from the connection.


### account\()

Retrieve information about the currently active Bluzelle account.

Returns JSON object representing the account information, e.g.

```
{ address: 'bluzelle1lgpau85z0hueyz6rraqqnskzmcz4zuzkfeqls7',
  coins: [ { denom: 'bnt', amount: '9899567400' } ],
  public_key: 'bluzellepub1addwnpepqd63w08dcrleyukxs4kq0n7ngalgyjdnu7jpf5khjmpykskyph2vypv6wms',
  account_number: 3,
  sequence: 218 }
```

Throws an exception if a response is not received from the connection.


### create\(key, value, gasInfo, leaseInfo\)

Create a field in the database.

| Argument | Description |
| :--- | :--- |
| key | The name of the key to create |
| value | The string value to set the key |
| gas_info | Object containing gas parameters (see above) |
| lease_info | Minimum time for key to remain in database (see above) |

Throws an exception when a response is not received from the connection, the key already exists, or invalid value.


### read\(key, prove\)

Retrieve the value of a key without consensus verification. Can optionally require the result to have a cryptographic proof (slower).

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve |
| prove | A proof of the value is required from the network (requires 'config trust-node false' to be set) |

Returns string value of the key.
Returns null if the key does not exist in the database.

Throws an exception when the prove is true and the result fails verification.


### txRead\(key, gasInfo\)

Retrieve the value of a key via a transaction (i.e. uses consensus).

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve |
| gasInfo | Object containing gas parameters (see above) |

Returns string value of the key.

Throws an exception when the key does not exist in the database.


### update\(key, value, gasInfo, leaseInfo\)

Update a field in the database.

| Argument | Description |
| :--- | :--- |
| key | The name of the key to create |
| value | The string value to set the key |
| gasInfo | Object containing gas parameters (see above) |
| leaseInfo | Positive or negative amount of time to alter the lease by. |

Throws an exception when the key doesn't exist, or invalid value.


### delete\(key, gasInfo\)

Delete a field from the database.

| Argument | Description |
| :--- | :--- |
| key | The name of the key to delete |
| gasInfo | Object containing gas parameters (see above) |

Throws an exception when the key is not in the database.


### has\(key\)

Query to see if a key is in the database. This function bypasses the consensus and cryptography mechanisms in favor of speed.

| Argument | Description |
| :--- | :--- |
| key | The name of the key to query |

Returns a boolean value - `true` or `false`, representing whether the key is in the database.


### txHas\(key, gasInfo\)

Query to see if a key is in the database via a transaction (i.e. uses consensus).

| Argument | Description |
| :--- | :--- |
| key | The name of the key to query |
| gasInfo | Object containing gas parameters (see above) |

Returns a boolean value - `true` or `false`, representing whether the key is in the database.


### keys\(\)

Retrieve a list of all keys. This function bypasses the consensus and cryptography mechanisms in favor of speed.

Returns an array of strings. ex. `["key1", "key2", ...]`.


### txKeys\(gasInfo\)

Retrieve a list of all keys via a transaction (i.e. uses consensus).

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above) |

Returns aa array of strings. ex. `["key1", "key2", ...]`.


### rename\(key, newKey, gasInfo\)

Change the name of an existing key.

| Argument | Description |
| :--- | :--- |
| key | The name of the key to rename |
| newKey | The new name for the key |
| gasInfo | Object containing gas parameters (see above) |

Throws an exception if the key doesn't exist.


### count\(\)

Retrieve the number of keys in the current database/uuid. This function bypasses the consensus and cryptography mechanisms in favor of speed.

Returns an integer value.


### txCount\(gasInfo\)

Retrieve the number of keys in the current database/uuid via a transaction.

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above) |

Returns an integer value.


### deleteAll\(gasInfo\)

Remove all keys in the current database/uuid.

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above) |


### keyValues\(\)

Enumerate all keys and values in the current database/uuid. This function bypasses the consensus and cryptography mechanisms in favor of speed.


Returns a map containing key/value pairs, e.g.

```
[{"key": "key1", "value": "value1"}, {"key": "key2", "value": "value2"}]
```

### txKeyValues\(gasInfo\)

Enumerate all keys and values in the current database/uuid via a transaction.

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above) |

Returns a map containing key/value pairs, e.g.

```
[("key1", "value1"), ("key2", "value2")]
```

### multiUpdate\(keyValues, gasInfo\)

Update multiple fields in the database.

| Argument | Description |
| :--- | :--- |
| keyValues | A map of keys and values |
| gasInfo | Object containing gas parameters (see above) |

Throws an exception when any of the keys doesn't exist.


### getLease\(key\)

Retrieve the minimum time remaining on the lease for a key. This function bypasses the consensus and cryptography mechanisms in favor of speed.

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve the lease information for |

Returns the minimum length of time remaining for the key's lease, in seconds.

Throws an exception when the key does not exist in the database.


### txGetLease\(key, gasInfo\)

Retrieve the minimum time remaining on the lease for a key, using a transaction.

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve the lease information for |
| gasInfo | Object containing gas parameters (see above) |

Returns the minimum length of time remaining for the key's lease, in seconds.

Throws an exception when the key does not exist in the database.


### renewLease\(key, gasInfo, leaseInfo\)

Update the minimum time remaining on the lease for a key.

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve the lease information for |
| gasInfo | Object containing gas parameters (see above) |
| leaseInfo | Minimum time for key to remain in database (see above) |

Throws an exception when the key does not exist in the database.


### renewLeaseAll\(gasInfo, leaseInfo\)

Update the minimum time remaining on the lease for all keys.

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above) |
| leaseInfo | Minimum time for key to remain in database (see above) |

Throws an exception when the key does not exist in the database.


### getNShortestLeases\(n\)

Retrieve a list of the n keys in the database with the shortest leases.  This function bypasses the consensus and cryptography mechanisms in favor of speed.

| Argument | Description |
| :--- | :--- |
| n | The number of keys to retrieve the lease information for |

Returns a map of keys and leases (in seconds), e.g.
```
[("key1", 12345), ("key2", 100500)]
```

### txGetNShortestLeases\(n, gasInfo\)

Retrieve a list of the N keys/values in the database with the shortest leases, using a transaction.

| Argument | Description |
| :--- | :--- |
| n | The number of keys to retrieve the lease information for |
| gasInfo | Object containing gas parameters (see above) |

Returns a map of keys and leases (in seconds), e.g.
```
[("key1", 12345), ("key2", 100500)]
```
