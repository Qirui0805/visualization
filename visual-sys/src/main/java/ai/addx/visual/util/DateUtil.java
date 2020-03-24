package ai.addx.visual.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateUtil {
    public static String LONDON = "Europe/London";
    public static String SHANGHAI = "Asia/Shanghai";

    public static long HOUR = 60 * 60 * 1000;
    public static long DAY = 24 * HOUR;

    public static SimpleDateFormat DEFAULT_SHANGHAI = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss"));
    public static SimpleDateFormat T_SHANGHAI = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat TZ_SHANGHAI = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static SimpleDateFormat DEFAULT_LONDON = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss"));
    public static SimpleDateFormat T_LONDON = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat TZ_LONDON = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        DEFAULT_SHANGHAI.setTimeZone(TimeZone.getTimeZone(SHANGHAI));
        T_SHANGHAI.setTimeZone(TimeZone.getTimeZone(SHANGHAI));
        TZ_SHANGHAI.setTimeZone(TimeZone.getTimeZone(SHANGHAI));

        DEFAULT_LONDON.setTimeZone(TimeZone.getTimeZone(LONDON));
        T_LONDON.setTimeZone(TimeZone.getTimeZone(LONDON));
        TZ_LONDON.setTimeZone(TimeZone.getTimeZone(LONDON));
    }
    public synchronized static String outTransform(String date){
        Date parse = null;
        try {
            parse = TZ_LONDON.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DEFAULT_SHANGHAI.format(parse);
    }
    public synchronized static String inTransform(String date) {
        Date parse = null;
        try {
            parse = DEFAULT_SHANGHAI.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return T_LONDON.format(parse);
    }

    public static String formatTransform(String date, SimpleDateFormat format1, SimpleDateFormat format2) {
        Date parse = null;
        try {
            parse = format1.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format2.format(parse);
    }

    static long compare(String date1, String date2) {
        long res = 0;
        try {
            res = DEFAULT_SHANGHAI.parse(date1).getTime() - DEFAULT_SHANGHAI.parse(date2).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public synchronized static long getTime(String time, SimpleDateFormat format) {
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
    public synchronized static String getString(long time, SimpleDateFormat format) {
        Date date = new Date(time);
        return format.format(date);
    }

    public static String calTimeString(String ordinary, SimpleDateFormat format, long interval) {
        return getString(getTime(ordinary, format) + interval, format);
    }

    public static long compare(String time1, String time2, SimpleDateFormat format) {
        return getTime(time1, format) - getTime(time2, format);
    }

    public synchronized static List<String> split(String from, String to, SimpleDateFormat format, long interval) {
        List<String> time = new ArrayList<>();
        time.add(from);
        String curr = calTimeString(from, format, interval);
        while (compare(to, curr, format) > 0) {
            time.add(curr);
            curr = calTimeString(curr, format, interval);
        }
        time.add(to);
        return time;
    }
}
