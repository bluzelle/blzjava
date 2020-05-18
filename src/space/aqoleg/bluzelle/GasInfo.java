package space.aqoleg.bluzelle;

@SuppressWarnings("WeakerAccess")
public class GasInfo {
    public final int maxGas;
    public final int maxFee;
    public final int gasPrice;

    /**
     * object containing parameters related to gas consumption
     *
     * @param maxGas   maximum amount of gas to consume
     * @param maxFee   maximum amount to charge, ubnt
     * @param gasPrice maximum price to pay for gas, ubnt
     * @throws IllegalArgumentException if any of values is negative
     */
    public GasInfo(int maxGas, int maxFee, int gasPrice) {
        if (maxGas < 0) {
            throw new IllegalArgumentException("negative maxGas");
        }
        if (maxFee < 0) {
            throw new IllegalArgumentException("negative maxFee");
        }
        if (gasPrice < 0) {
            throw new IllegalArgumentException("negative gasPrice");
        }
        this.maxGas = maxGas;
        this.maxFee = maxFee;
        this.gasPrice = gasPrice;
    }
}