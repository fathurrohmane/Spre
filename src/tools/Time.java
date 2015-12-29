package tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Fathurrohman on 18-Dec-15.
 */
public class Time {

    public static String getTime() {
        String time;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        time = simpleDateFormat.format(calendar.getTime());
        return "("+time+"): ";
    }
}
