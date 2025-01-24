package cc.qi7.esp8266_test.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author AHeng
 * @date 2024/08/16 23:22
 */
public class Clone {
    
    /**
     * 深度拷贝List集合
     *
     * @param sourceList 被拷贝的集合
     * @param <T>        sourceList中的T必须实现Serializable接口
     * @return 复制出来的新集合，修改原集合的话不会受原集合的影响
     */
    public static <T extends Serializable> List<T> deepCopyList(List<T> sourceList) throws IOException, ClassNotFoundException {
        if (sourceList == null) {
            return null;
        }
        
        if (sourceList.isEmpty()) {
            return sourceList;
        }
        
        // 使用try-with-resources会自动回收各种Stream
        try (ByteArrayOutputStream bo = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bo)) {
            oos.writeObject(sourceList);
            try (ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(bi)) {
                @SuppressWarnings("unchecked")
                List<T> dest = (List<T>) ois.readObject();
                return dest;
            }
        }
    }
    
}
