import onethreeseven.ydlbot.model.DuelistLocator;

/**
 * Entry point for the bot.
 * @author Luke Bermingham
 */
public class Main {

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadShared();
        new DuelistLocator().findDuelist();

    }

}
