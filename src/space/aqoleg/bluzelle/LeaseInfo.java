package space.aqoleg.bluzelle;

public class LeaseInfo {
    static final int blockTimeSeconds = 5;
    final int blocks;

    /**
     * object containing parameters related to the minimum time a key should be maintained in the database
     *
     * @param days    number of days
     * @param hours   number of hours
     * @param minutes number of minute
     * @param seconds number of seconds
     */
    public LeaseInfo(int days, int hours, int minutes, int seconds) {
        blocks = (seconds + (minutes + (hours + days * 24) * 60) * 60) / blockTimeSeconds;
    }
}