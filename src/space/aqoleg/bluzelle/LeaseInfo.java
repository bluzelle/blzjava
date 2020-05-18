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
     * object containing parameters related to the minimum time a key should be maintained in the database
     *
     * @param days    number of days
     * @param hours   number of hours
     * @param minutes number of minute
     * @param seconds number of seconds
     */
    public LeaseInfo(int days, int hours, int minutes, int seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        blocks = (seconds + (minutes + (hours + days * 24) * 60) * 60) / blockTimeSeconds;
    }
}