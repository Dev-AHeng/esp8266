package cc.qi7.esp8266_test.utils;

import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author AHeng
 * @date 2024/08/16 20:17
 */
public class BaseUtils {
    private static final String TAG = "日志";
    
    /**
     * 计算代码执行时间
     *
     * @param action 要执行的代码
     * @use runTime(() -> {})
     */
    public static void getRunTime(Runnable action) {
        // 开始时间
        long startTime = System.nanoTime();
        action.run();
        // 结束时间
        long endTime = System.nanoTime();
        System.out.println("执行时间：" + TimeUnit.NANOSECONDS.toMillis(endTime - startTime) + "毫秒");
    }
    
    
    /**
     * 判断字符串是否是JSON格式
     *
     * @param jsonString 要判断的字符串
     * @return 是否是JSON格式
     */
    public static boolean isJson(String jsonString) {
        try {
            Map map = JSON.parseObject(jsonString, Map.class);
            return map != null && !map.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isNotJson(String jsonString) {
        return !isJson(jsonString);
    }
    
    
}
