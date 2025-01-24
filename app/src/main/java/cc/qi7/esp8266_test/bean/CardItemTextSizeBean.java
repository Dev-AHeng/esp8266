package cc.qi7.esp8266_test.bean;

import androidx.annotation.NonNull;

import java.util.StringJoiner;

/**
 * @author AHeng
 * @date 2024/09/19 0:31
 */
public class CardItemTextSizeBean {
    private float prefixTextSize;
    
    private float placeholderTextSize;
    
    private float suffixTextSize;
    
    private float timeTextSize;
    
    public CardItemTextSizeBean(float prefixTextSize, float placeholderTextSize, float suffixTextSize, float timeTextSize) {
        this.prefixTextSize = prefixTextSize;
        this.placeholderTextSize = placeholderTextSize;
        this.suffixTextSize = suffixTextSize;
        this.timeTextSize = timeTextSize;
    }
    
    public float getPrefixTextSize() {
        return prefixTextSize;
    }
    
    public void setPrefixTextSize(float prefixTextSize) {
        this.prefixTextSize = prefixTextSize;
    }
    
    public float getPlaceholderTextSize() {
        return placeholderTextSize;
    }
    
    public void setPlaceholderTextSize(float placeholderTextSize) {
        this.placeholderTextSize = placeholderTextSize;
    }
    
    public float getSuffixTextSize() {
        return suffixTextSize;
    }
    
    public void setSuffixTextSize(float suffixTextSize) {
        this.suffixTextSize = suffixTextSize;
    }
    
    public float getTimeTextSize() {
        return timeTextSize;
    }
    
    public void setTimeTextSize(float timeTextSize) {
        this.timeTextSize = timeTextSize;
    }
    
    @NonNull
    @Override
    public String toString() {
        return new StringJoiner(", ", CardItemTextSizeBean.class.getSimpleName() + "[", "]")
                       .add("prefixTextSize=" + prefixTextSize)
                       .add("placeholderTextSize=" + placeholderTextSize)
                       .add("suffixTextSize=" + suffixTextSize)
                       .add("timeTextSize=" + timeTextSize)
                       .toString();
    }
}
