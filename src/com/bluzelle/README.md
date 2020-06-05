<a href="https://bluzelle.com/">
    <img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/>
</a>


# Build

Build from the source code.
Get the package from github.

    $ git clone https://github.com/aqoleg/blzjava.git

Create output directory.

    $ mkdir blzjava/out

Compile.

    $ javac -cp blzjava/src/ -d blzjava/out/ blzjava/src/com/bluzelle/Bluzelle.java

Create file bluzelle.jar.

    $ cd blzjava/out/
    $ jar cf ../../bluzelle.jar ./
    $ cd ../../



# API documentation

#### Gas info

Some API functions take `gasInfo` as a parameter. This is an object containing parameters related to gas consumption.

```java
GasInfo gasInfo = new GasInfo(gasPrice, maxGas, maxFee);
```
* gasPrice - maximum price to pay for gas in ubnt, integer
* maxGas - maximum amount of gas to consume for this call, integer
* maxFee - maximum amount to charge for this call in ubnt, integer

All values should be non-negative. The `maxGas` value will always be honored if present, otherwise a default value will be used. If both `maxFee` and `gasPrice` are positive, `gasPrice` will be ignored and calculated based on the provided `maxFee`.

#### Lease info

Some API functions take `leaseInfo` as a parameter. This is an object containing parameters related to the minimum time a key should be maintained in the database.

```java
LeaseInfo leaseInfo = new LeaseInfo(days, hours, minutes, seconds);
```

#### Handle exceptions

All functions can throw exceptions.

```java
try {
    bluzelle.delete("key", new GasInfo(10, 0, 0));
} catch (Connection.ConnectionException e) {
    // cannot establish connection
    System.out.println(e.getMessage());
} catch (Bluzelle.ServerException e) {
    // server returns error
    System.out.println(e.getMessage());
}
```


## connect\(mnemonic, endpoint, uuid, chainId\)

Configures the Bluzelle connection. This may be called multiple times to create multiple clients.

```java
Bluzelle bluzelle = Bluzelle.connect(
    "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
    "http://testnet.public.bluzelle.com:1317",
    "20fc19d4-7c9d-4b5c-9578-8cedd756e0ea",
    "bluzelle"
);
```

| Argument | Description |
| :--- | :--- |
| **mnemonic** | The mnemonic of the private key for your Bluzelle account. String. |
| **endpoint** | The hostname and port of your rest server. String or null for default "http://localhost:1317" |
| **uuid** | Bluzelle uses `UUID`'s to identify distinct databases on a single swarm. We recommend using [version 4 of the universally unique identifier](https://en.wikipedia.org/wiki/Universally_unique_identifier#Version_4_%28random%29). String or null for default the same as address. |
| **chainId** | The chain id of your Bluzelle accoun. String or null for default "bluzelle". |

Returns instance of Bluzelle for calling other functions.


### version\()

Retrieve the version of the Bluzelle service.

```java
String version = bluzelle.version();
```

Returns String containing the version information.


### account\()

Retrieve information about the currently active Bluzelle account.

```java
JsonObject account = bluzelle.account();
```

Returns JsonObject representing the account information.


### create\(key, value, gasInfo, leaseInfo\)

Create a field in the database.

```java
bluzelle.create(key, value, gasInfo, leaseInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The name of the key to create. String. |
| value | The value to set the key. String. |
| gasInfo | Object containing gas parameters (see above). |
| leaseInfo | Object containing minimum time for key to remain in database (see above). |

Returns nothing.


### read\(key, prove\)

Retrieve the value of a key without consensus verification.

```java
String value = bluzelle.read(key, prove);
```

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve. String. |
| prove | A proof of the value is required from the network. Boolean. |

Returns String value of the key or null if the key does not exist in the database.


### txRead\(key, gasInfo\)

Retrieve the value of a key via a transaction (i.e. uses consensus).

```java
String value = bluzelle.txRead(key, gasInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve. String. |
| gasInfo | Object containing gas parameters (see above). |

Returns String value of the key.


### update\(key, value, gasInfo, leaseInfo\)

Update a field in the database.

```java
bluzelle.update(key, value, gasInfo, leaseInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The name of the key to update. String. |
| value | The value to set the key. String. |
| gasInfo | Object containing gas parameters (see above). |
| leaseInfo | Object containing positive or negative amount of time to alter the lease by or null. |

Returns nothing.


### delete\(key, gasInfo\)

Delete a field from the database.

```java
bluzelle.delete(key, gasInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The name of the key to delete. String. |
| gasInfo | Object containing gas parameters (see above). |

Returns nothing.


### has\(key\)

Query to see if a key is in the database. This function bypasses the consensus and cryptography mechanisms in favor of speed.

```java
boolean has = bluzelle.has(key);
```

| Argument | Description |
| :--- | :--- |
| key | The name of the key to query. String. |

Returns a boolean, representing whether the key is in the database.


### txHas\(key, gasInfo\)

Query to see if a key is in the database via a transaction (i.e. uses consensus).

```java
boolean has = bluzelle.txHas(key, gasInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The name of the key to query. String. |
| gasInfo | Object containing gas parameters (see above). |

Returns a boolean, representing whether the key is in the database.


### keys\(\)

Retrieve a list of all keys. This function bypasses the consensus and cryptography mechanisms in favor of speed.

```java
ArrayList<String> keys = bluzelle.keys();
```

Returns an ArrayList of keys as String.


### txKeys\(gasInfo\)

Retrieve a list of all keys via a transaction (i.e. uses consensus).

```java
ArrayList<String> keys = bluzelle.txKeys(gasInfo);
```

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above). |

Returns an ArrayList of keys as String.


### rename\(key, newKey, gasInfo\)

Change the name of an existing key.

```java
bluzelle.rename(key, newKey, gasInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The name of the key to rename. String. |
| newKey | The new name for the key. String. |
| gasInfo | Object containing gas parameters (see above). |

Returns nothing.


### count\(\)

Retrieve the number of keys in the current database/uuid. This function bypasses the consensus and cryptography mechanisms in favor of speed.

```java
int count = bluzelle.count();
```

Returns an integer value.


### txCount\(gasInfo\)

Retrieve the number of keys in the current database/uuid via a transaction.

```java
int count = bluzelle.txCount(gasInfo);
```

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above). |

Returns an integer value.


### deleteAll\(gasInfo\)

Remove all keys in the current database/uuid.

```java
bluzelle.deleteAll(gasInfo);
```

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above). |

Returns nothing.


### keyValues\(\)

Enumerate all keys and values in the current database/uuid. This function bypasses the consensus and cryptography mechanisms in favor of speed.

```java
HashMap<String, String> keyValues = bluzelle.keyValues();
```

Returns a HashMap containing key/value pairs as Strings.


### txKeyValues\(gasInfo\)

Enumerate all keys and values in the current database/uuid via a transaction.

```java
HashMap<String, String> keyValues = bluzelle.txKeyValues(gasInfo);
```

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above). |

Returns a HashMap containing key/value pairs as Strings.


### multiUpdate\(keyValues, gasInfo\)

Update multiple fields in the database.

```java
bluzelle.multiUpdate(keyValues, gasInfo);
```

| Argument | Description |
| :--- | :--- |
| keyValues | HashMap containing key/value pairs as Strings. |
| gasInfo | Object containing gas parameters (see above). |

Returns nothing.


### getLease\(key\)

Retrieve the minimum time remaining on the lease for a key. This function bypasses the consensus and cryptography mechanisms in favor of speed.

```java
int lease = bluzelle.getLease(key);
```

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve the lease information for. String. |

Returns the minimum length of time remaining for the key's lease, integer, in seconds.


## txGetLease\(key, gasInfo\)

Retrieve the minimum time remaining on the lease for a key, using a transaction.

```java
int lease = bluzelle.txGetLease(key, gasInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve the lease information for. String. |
| gasInfo | Object containing gas parameters (see above). |

Returns the minimum length of time remaining for the key's lease, integer, in seconds.


### renewLease\(key, gasInfo, leaseInfo\)

Update the minimum time remaining on the lease for a key.

```java
bluzelle.renewLease(key, gasInfo, leaseInfo);
```

| Argument | Description |
| :--- | :--- |
| key | The key to retrieve the lease information for. String. |
| gasInfo | Object containing gas parameters (see above). |
| leaseInfo | Object containing minimum time for key to remain in database (see above) or null. |

Returns nothing.


### renewLeaseAll\(gasInfo, leaseInfo\)

Update the minimum time remaining on the lease for all keys.

```java
bluzelle.renewLeaseAll(gasInfo, leaseInfo);
```

| Argument | Description |
| :--- | :--- |
| gasInfo | Object containing gas parameters (see above). |
| leaseInfo | Object containing minimum time for key to remain in database (see above) or null. |

Returns nothing.


### getNShortestLeases\(n\)

Retrieve a list of the n keys in the database with the shortest leases.  This function bypasses the consensus and cryptography mechanisms in favor of speed.

```java
HashMap<String, Integer> leases = bluzelle.getNShortestLeases(n);
```

| Argument | Description |
| :--- | :--- |
| n | The number of keys to retrieve the lease information for. Integer. |

Returns HashMap containing key/lease pairs as String/Integer (seconds).


### txGetNShortestLeases\(n, gasInfo\)

Retrieve a list of the n keys in the database with the shortest leases, using a transaction.

```java
HashMap<String, Integer> leases = bluzelle.txGetNShortestLeases(n, gasInfo);
```

| Argument | Description |
| :--- | :--- |
| n | The number of keys to retrieve the lease information for. Integer. |
| gasInfo | Object containing gas parameters (see above). |

Returns HashMap containing key/lease pairs as String/Integer (seconds).
