package cc.qi7.esp8266_test.utils;

import java.util.List;
import java.util.Random;

public class RandomUtil {
    
    /**
     * 随机返回[min, max]之间的值
     *
     * @param min 最小随机数
     * @param max 最大随机数
     * @return [min, max]之间的值
     */
    public static int getRandomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("最大值必须大于或等于最小值");
        }
        return min + (int) (Math.random() * (max - min + 1));
    }
    
    /**
     * 从列表中随机选择一个元素。
     *
     * @param list 要随机选择元素的列表
     * @param <T>  列表元素的类型
     * @return 一个随机选择的元素
     */
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("列表不得为 null 或为空");
        }
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}