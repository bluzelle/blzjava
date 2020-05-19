package space.aqoleg.bluzelle;

@SuppressWarnings("WeakerAccess")
public class GasInfo {
    public final int gasPrice;
    public final int maxGas;
    public final int maxFee;

    /**
     * object containing parameters related to gas consumption
     *
     * @param gasPrice maximum price to pay for gas, ubnt
     * @param maxGas   maximum amount of gas to consume
     * @param maxFee   maximum amount to charge, ubnt
     * @throws IllegalArgumentException if any of values is negative
     */
    public GasInfo(int gasPrice, int maxGas, int maxFee) {
        if (gasPrice < 0) {
            throw new IllegalArgumentException("negative gasPrice");
        }
        if (maxGas < 0) {
            throw new IllegalArgumentException("negative maxGas");
        }
        if (maxFee < 0) {
            throw new IllegalArgumentException("negative maxFee");
        }
        this.maxGas = maxGas;
        this.maxFee = maxFee;
        this.gasPrice = gasPrice;
    }
}