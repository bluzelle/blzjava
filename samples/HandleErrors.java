import space.aqoleg.bluzelle.*;

public class HandleErrors {
    public static void main(String[] args) {
        Bluzelle bluzelle;
        try {
            bluzelle = Bluzelle.getInstance(
                    "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                    "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                    "http://testnet.public.bluzelle.com:1317",
                    null,
                    null
            );
        } catch (EndpointException e) {
            System.out.println("incorrect endpoint");
            return;
        } catch (ConnectionException e) {
            System.out.println("can not connect");
            return;
        }

        try {
            bluzelle.delete("key", new GasInfo(0, 0, 1000));
        } catch (ConnectionException e) {
            System.out.println("can not connect");
        } catch (ServerException e) {
            System.out.println("server error " + e);
        }

        try {
            bluzelle.create("key", "value", new GasInfo(0, 0, 0), null);
        } catch (ConnectionException e) {
            System.out.println("can not connect");
        } catch (ServerException e) {
            System.out.println("server error " + e);
        }

        try {
            System.out.println(bluzelle.count());
        } catch (ConnectionException e) {
            System.out.println("can not connect");
        }
    }
}
