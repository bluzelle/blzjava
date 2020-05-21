// object containing parameters related to the minimum time a key should be maintained in the database
// usage:
//    LeaseInfo leaseInfo = new LeaseInfo(days, hours, minutes, seconds);
//    int days = leaseInfo.days;
//    int hours = leaseInfo.hours;
//    int minutes = leaseInfo.minutes;
//    int seconds = leaseInfo.seconds;
//    int blocks = leaseInfo.blocks;
package space.aqoleg.bluzelle;

@SuppressWarnings("WeakerAccess")
public class LeaseInfo {
    public static final int blockTimeSeconds = 5;
    public final int days;
    public final int hours;
    public final int minutes;
    public final int seconds;
    public final int blocks;

    /**
     * @param days    number of days
     * @param hours   number of hours
     * @param minutes number of minute
     * @param seconds number of seconds
     * @throws IllegalArgumentException if total time is negative
     */
    public LeaseInfo(int days, int hours, int minutes, int seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        blocks = (seconds + (minutes + (hours + days * 24) * 60) * 60) / blockTimeSeconds;
        if (blocks < 0) {
            throw new IllegalArgumentException("negative lease");
        }
    }
}