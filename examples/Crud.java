import com.bluzelle.Bluzelle;
import com.bluzelle.GasInfo;

public class Crud {

    public static void main(String[] args) {
        if (args.length > 1) {
            switch (args[0]) {
                case "-c":
                    if (args.length == 3) {
                        create(args[1], args[2]);
                    } else {
                        printUsage();
                    }
                    break;
                case "-r":
                    if (args.length == 2) {
                        read(args[1]);
                    } else {
                        printUsage();
                    }
                    break;
                case "-u":
                    if (args.length == 3) {
                        update(args[1], args[2]);
                    } else {
                        printUsage();
                    }
                    break;
                case "-d":
                    if (args.length == 2) {
                        delete(args[1]);
                    } else {
                        printUsage();
                    }
                    break;
                default:
                    printUsage();
            }
        } else {
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java Crud -option args...");
        System.out.println("where option is one of:");
        System.out.println("-c <key> <value>    create specified key-value pair");
        System.out.println("-r <key>            read value of the specified key");
        System.out.println("-u <key> <value>    update specified key with new value");
        System.out.println("-d <key>            delete specified key");
    }

    private static void create(String key, String value) {
        connect().create(key, value, new GasInfo(100, 0, 0), null);
        System.out.println("created");
    }

    private static void read(String key) {
        System.out.println(connect().read(key, false));
    }

    private static void update(String key, String value) {
        connect().update(key, value, new GasInfo(100, 0, 0), null);
        System.out.println("updated");
    }

    private static void delete(String key) {
        connect().delete(key, new GasInfo(100, 0, 0));
        System.out.println("deleted");
    }

    private static Bluzelle connect() {
        String mnemonic = "around buzz diagram captain obtain detail salon mango muffin brother" +
                " morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car";
        String endpoint = "http://testnet.public.bluzelle.com:1317";
        return Bluzelle.connect(mnemonic, endpoint, null, null);
    }
}