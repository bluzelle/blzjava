import space.aqoleg.bluzelle.Bluzelle;
import space.aqoleg.bluzelle.GasInfo;
import space.aqoleg.bluzelle.LeaseInfo;

public class FirstStep {

    public static void main(String[] args) {
        Bluzelle bluzelle = Bluzelle.getInstance(
                "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                "http://testnet.public.bluzelle.com:1317",
                null,
                null
        );

        GasInfo gasInfo = new GasInfo(0, 0, 30);
        LeaseInfo leaseInfo = new LeaseInfo(1, 0, 0, 0);

        bluzelle.create("key", "value", gasInfo, leaseInfo);
        bluzelle.update("key", "new value", gasInfo, leaseInfo);
        System.out.println(bluzelle.read("key", false));
        bluzelle.delete("key", gasInfo);
        System.out.println(bluzelle.has("key"));
    }
}
