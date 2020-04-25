import space.aqoleg.bluzelle.Bluzelle;
import space.aqoleg.bluzelle.GasInfo;
import space.aqoleg.bluzelle.LeaseInfo;

public class MultiThread {
    private static final String[] keys = {"one", "two", "three", "four", "five", "six", "seven"};
    private GasInfo gasInfo = new GasInfo(0, 0, 900);
    private LeaseInfo leaseInfo = new LeaseInfo(1, 0, 0, 0);
    private int keyN = 0;

    public static void main(String[] args) {
        MultiThread multiThread = new MultiThread();
        multiThread.write();
        multiThread.write();
        multiThread.write();
        multiThread.read();
    }

    private void write() {
        new Thread(() -> {
            Bluzelle bluzelle = Bluzelle.getInstance(
                    "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                    "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                    "http://testnet.public.bluzelle.com:1317",
                    null,
                    null
            );
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (keys) {
                    if (keyN == keys.length) {
                        return;
                    }
                    bluzelle.create(keys[keyN], "something", gasInfo, leaseInfo);
                    System.out.println(Thread.currentThread().toString() + ", " + keys[keyN]);
                    keyN++;
                }
            } while (true);
        }).start();
    }

    private void read() {
        new Thread(() -> {
            Bluzelle bluzelle = Bluzelle.getInstance(
                    "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                    "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                    "http://testnet.public.bluzelle.com:1317",
                    null,
                    null
            );
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bluzelle.keyValues().forEach((key, value) -> System.out.println(key + ":" + value));
            }
            bluzelle.deleteAll(gasInfo);
        }).start();
    }
}
