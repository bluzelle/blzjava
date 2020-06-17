<a href="https://bluzelle.com/">
    <img src='https://raw.githubusercontent.com/bluzelle/api/master/source/images/Bluzelle%20-%20Logo%20-%20Big%20-%20Colour.png' alt="Bluzelle" style="width: 100%"/>
</a>

**blzjava** is a Java/Android library that can be used to access the Bluzelle database service.

* Build and API [docs](src/com/bluzelle)
* Java [docs](https://aqoleg.github.io/blzjava)
* [Examples](examples)
* Android [example](android)
* [Tests](test/com/bluzelle)


# Quickstart

Make sure that Java is installed and has a version at least 8 (JDK 1.8).

    $ java -version
    $ javac -version

If Java is not installed, install it.

    $ sudo apt-get update
    $ sudo apt-get install default-jdk

Download bluzelle.jar from [releases](https://github.com/bluzelle/blzjava/releases) or using terminal.

    $ wget https://github.com/bluzelle/blzjava/releases/download/0.0.1/bluzelle.jar

Create file "Quickstart.java".
```java
import com.bluzelle.*;

public class Quickstart {

    public static void main(String[] args) {
        Bluzelle bluzelle = Bluzelle.connect(
                "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                "http://dev.testnet.public.bluzelle.com:1317",
                null,
                null
        );

        GasInfo gasInfo = new GasInfo(10, 0, 0);
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

    $ javac -cp ./bluzelle.jar Quickstart.java

Run.

    $ java -cp .:./bluzelle.jar Quickstart


