package space.aqoleg.bluzelle;

public class GasInfo {
    final int maxGas;
    final int maxFee;
    final int gasPrice;

    /**
     * object containing parameters related to gas consumption
     *
     * @param maxGas   maximum amount of gas to consume
     * @param maxFee   maximum amount to charge, ubnt
     * @param gasPrice maximum price to pay for gas, ubnt
     */
    public GasInfo(int maxGas, int maxFee, int gasPrice) {
        this.maxGas = maxGas;
        this.maxFee = maxFee;
        this.gasPrice = gasPrice;
    }
}