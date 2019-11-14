import com.cwj.express.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeTest {

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++){
            LocalDateTime now = LocalDateTime.now();
            String timeString = LocalDateTimeUtils.formatToYMDHMS(now);
            LocalDateTime old = LocalDateTimeUtils.ymdhmsParseToLocalDataTime(timeString);
            if (!now.isEqual(old)){
                System.out.println("***** " + i);
                System.out.println(now);
                System.out.println(timeString);
            }
        }
    }
}
