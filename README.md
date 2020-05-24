<a href="https://bluzelle.com/">
    <img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/>
</a>

**blzjava** is a Java/Android library that can be used to access the Bluzelle database service.

* [API docs](src)
* [Examples](examples)
* Android [examples](android)
* [Tests](test)


# Quickstart

Make sure that JDK is installed.

    $ java -version
    $ javac -version

If JDK is not installed, install it.

    $ sudo apt-get update
    $ sudo apt-get install default-jdk

Download the [library](https://github.com/aqoleg/blzjava/releases/download/1.3.0/java.jar).

    $ wget https://github.com/aqoleg/blzjava/releases/download/1.3.0/java.jar

Use your favorite IDE or text editor to create "Quickstart.java" file.
```java
import space.aqoleg.bluzelle.*;

public class Quickstart {

    public static void main(String[] args) {
        Bluzelle bluzelle = Bluzelle.connect(
                "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                "http://testnet.public.bluzelle.com:1317",
                null,
                null
        );

        GasInfo gasInfo = new GasInfo(100, 0, 0);
        LeaseInfo leaseInfo = new LeaseInfo(1, 0, 0, 0);

        bluzelle.create("key", "value", gasInfo, leaseInfo);
        System.out.println("created");
        bluzelle.update("key", "new value", gasInfo, leaseInfo);
        System.out.println("updated");
        System.out.println("'key': " + bluzelle.read("key", false));
        bluzelle.delete("key", gasInfo);
        System.out.println("deleted");
    }
}
```

Compile.

    $ javac -cp <path_to_library> Quickstart.java

Run.

    $ java -cp .:<path_to_library> Quickstart
