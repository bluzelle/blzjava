import space.aqoleg.bluzelle.*;

public class Threads {

    public static void main(String[] args) {
        new Thread(() -> {
            Bluzelle bluzelle = Bluzelle.getInstance(
                    "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                    "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                    "http://testnet.public.bluzelle.com:1317",
                    null,
                    null
            );

            for (int i = 0; i < 30; i++) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                System.out.println(bluzelle.count() + " keys");
            }
        }).start();

        String[] keys = {"one", "two", "three", "four", "five", "six", "seven"};
        int keyN = 0;
        Bluzelle bluzelle = Bluzelle.getInstance(
                "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                "http://testnet.public.bluzelle.com:1317",
                null,
                null
        );
        do {
            bluzelle.create(
                    keys[keyN],
                    "something",
                    new GasInfo(0, 0, 900),
                    new LeaseInfo(1, 0, 0, 0)
            );
            System.out.println("write " + keys[keyN]);
        } while (++keyN != keys.length);
        bluzelle.deleteAll(new GasInfo(0, 0, 900));
    }
}
