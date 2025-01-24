package cc.qi7.esp8266_test.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Dev_Heng
 * @date 2024年8月20日15:08:17
 */
public class DateTimeUtil {
    
    /**
     * private static final String TIME_PATTERN = "yyyy年MM月dd日 HH时mm分ss秒";
     */
    // private static final String TIME_PATTERN = "mm分ss秒SSS毫秒";
    
    private static final String TIME_PATTERN = "yy年MM月dd日HH:mm:ss";
    
    /**
     * 获取当前时间的格式化字符串
     *
     * @param pattern 格式化模式，例如 "yyyy年MM月dd日 HH时mm分ss秒"
     * @return 格式化的当前时间字符串
     */
    public static String getCurrentFormattedTime(String pattern) {
        // 获取当前时间
        Date now = new Date();
        
        // 使用SimpleDateFormat进行格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        
        // 返回格式化的时间字符串
        return simpleDateFormat.format(now);
    }
    
    public static String getCurrentFormattedTime() {
        return getCurrentFormattedTime(TIME_PATTERN);
    }
    
}