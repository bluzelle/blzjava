import com.bluzelle.Bluzelle;
import com.bluzelle.Connection;
import com.bluzelle.GasInfo;
import com.bluzelle.LeaseInfo;

public class Threads {
    private final String mnemonic = "around buzz diagram captain obtain detail salon mango muffin brother" +
            " morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car";
    private final String endpoint = "http://dev.testnet.public.bluzelle.com:1317";
    private final String[] keys = {"one", "two", "three", "four", "five", "six", "seven", "eight", "100500"};
    private GasInfo gasInfo = new GasInfo(10, 0, 0);
    private LeaseInfo leaseInfo = new LeaseInfo(0, 0, 10, 0);

    public static void main(String[] args) {
        Threads threads = new Threads();
        threads.read();
        threads.write();
    }

    private void read() {
        new Thread(() -> {
            Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "uuid", "bluzelle");

            for (int i = 0; i < 30; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                System.out.println("keys: " + bluzelle.keys());
            }
        }).start();
    }

    private void write() {
        Bluzelle bluzelle = Bluzelle.connect(mnemonic, endpoint, "uuid", null);

        for (String key : keys) {
            System.out.println("creating key '" + key + "'");
            try {
                bluzelle.create(key, String.valueOf(Math.random()), gasInfo, leaseInfo);
            } catch (Connection.ConnectionException | Bluzelle.ServerException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("deleting all keys");
        bluzelle.deleteAll(new GasInfo(10, 0, 0));
    }
}
