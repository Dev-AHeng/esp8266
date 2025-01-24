package cc.qi7.esp8266_test.utils;

/**
 * @author Dev_Heng
 * @date 2024年9月18日00:59:01
 */
public class DebounceUtils {
    
    /**
     * 防抖间隔时间，800毫秒
     */
    private static final long DEBOUNCE_INTERVAL = 800;
    private static long lastClickTime;
    
    public static boolean isDebounce() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < DEBOUNCE_INTERVAL) {
            // 如果当前点击时间与上次点击时间差小于间隔时间，则返回true，表示点击过快
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}