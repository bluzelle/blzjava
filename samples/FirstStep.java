import space.aqoleg.bluzelle.*;

public class FirstStep {

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
        bluzelle.update("key", "new value", gasInfo, leaseInfo);
        System.out.println(bluzelle.read("key", false));
        bluzelle.delete("key", gasInfo);
        System.out.println(bluzelle.has("key"));
    }
}
