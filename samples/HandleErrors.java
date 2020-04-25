package space.aqoleg;

import space.aqoleg.bluzelle.Bluzelle;
import space.aqoleg.bluzelle.GasInfo;
import space.aqoleg.exception.BluzelleException;
import space.aqoleg.exception.ConnectionException;
import space.aqoleg.exception.EndpointException;
import space.aqoleg.exception.ResponseException;

public class HandleErrors {
    public static void main(String[] args) {
        Bluzelle bluzelle;
        try {
            bluzelle = connect("h://notestnet.public.bluzelle.com:1317");
        } catch (EndpointException e) {
            System.out.println("incorrect endpoint " + e.getMessage());
            bluzelle = connect("http://testnet.public.bluzelle.com:1317");
        } catch (ConnectionException e) {
            System.out.println("can not connect " + e.getMessage());
            bluzelle = connect("http://testnet.public.bluzelle.com:1317");
        }

        do {
            try {
                bluzelle.delete("key", new GasInfo(0, 0, 1000));
            } catch (ConnectionException e) {
                System.out.println("can not connect, wait and try again " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            } catch (ResponseException e) {
                System.out.println("server error " + e);
            } catch (BluzelleException e) {
                System.out.println("any other exception " + e);
            }
            return;
        } while (true);
    }

    private static Bluzelle connect(String endpoint) {
        return Bluzelle.getInstance(
                "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend knife carry green dwarf vendor hungry fan route pumpkin car",
                endpoint,
                null,
                null
        );
    }
}