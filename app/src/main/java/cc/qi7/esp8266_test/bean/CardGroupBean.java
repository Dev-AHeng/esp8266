package cc.qi7.esp8266_test.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author Dev_Heng
 * @date 2024/08/13 20:06
 */
public class CardGroupBean implements Serializable {
    /**
     * Serializable识别
     */
    private static final long serialVersionUID = 666666L;
    
    /**
     * 标好占位符的模版
     */
    private String placeholderModelContent;
    
    /**
     * 根据占位符切割模版得到的集合
     */
    private String[] placeholderSplit;
    
    /**
     * 占位符对应的CardItemBean
     */
    private Map<String, EditPlaceholderFragmentItemBean> cardItemBeanMap;
    
    public String getPlaceholderModelContent() {
        return placeholderModelContent;
    }
    
    public String[] getPlaceholderSplit() {
        return placeholderSplit;
    }
    
    public void setPlaceholderSplit(String[] placeholderSplit) {
        this.placeholderSplit = placeholderSplit;
    }
    
    public Map<String, EditPlaceholderFragmentItemBean> getCardItemBeanMap() {
        return cardItemBeanMap;
    }
    
    public void setCardItemBeanMap(Map<String, EditPlaceholderFragmentItemBean> cardItemBeanMap) {
        this.cardItemBeanMap = cardItemBeanMap;
    }
    
    public void setPlaceholderModelContent(String placeholderModelContent) {
        this.placeholderModelContent = placeholderModelContent;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardGroupBean)) {
            return false;
        }
        
        CardGroupBean that = (CardGroupBean) o;
        
        if (!Objects.equals(placeholderModelContent, that.placeholderModelContent)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(placeholderSplit, that.placeholderSplit)) {
            return false;
        }
        return Objects.equals(cardItemBeanMap, that.cardItemBeanMap);
    }
    
    @Override
    public int hashCode() {
        int result = placeholderModelContent != null ? placeholderModelContent.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(placeholderSplit);
        result = 31 * result + (cardItemBeanMap != null ? cardItemBeanMap.hashCode() : 0);
        return result;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "CardGroupBean{" +
                       "placeholderModelContent='" + placeholderModelContent + '\'' +
                       ", placeholderSplit=" + Arrays.toString(placeholderSplit) +
                       ", cardItemBeanMap=" + cardItemBeanMap +
                       '}';
    }
}
