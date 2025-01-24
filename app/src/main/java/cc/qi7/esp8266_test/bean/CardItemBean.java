package cc.qi7.esp8266_test.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author AHeng
 * @date 2024/08/13 20:04
 */
public class CardItemBean implements Serializable {
    /**
     * 标好占位符的模版
     */
    private String placeholderModelContent;
    
    /**
     * 根据占位符切割模版得到的集合
     */
    private String[] placeholderSplit;
    
    /**
     * 前缀
     */
    private String prefix;
    
    /**
     * 占位符
     */
    private String placeholder;
    
    /**
     * 后缀
     */
    private String suffix;
    
    /**
     * 卡片备注
     */
    private String comment;
    
    public CardItemBean() {
    }
    
    public CardItemBean(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getPlaceholderModelContent() {
        return placeholderModelContent;
    }
    
    public void setPlaceholderModelContent(String placeholderModelContent) {
        this.placeholderModelContent = placeholderModelContent;
    }
    
    public String[] getPlaceholderSplit() {
        return placeholderSplit;
    }
    
    public void setPlaceholderSplit(String[] placeholderSplit) {
        this.placeholderSplit = placeholderSplit;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardItemBean)) {
            return false;
        }
        
        CardItemBean that = (CardItemBean) o;
        
        if (!Objects.equals(placeholderModelContent, that.placeholderModelContent)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(placeholderSplit, that.placeholderSplit)) {
            return false;
        }
        if (!Objects.equals(prefix, that.prefix)) {
            return false;
        }
        if (!Objects.equals(placeholder, that.placeholder)) {
            return false;
        }
        if (!Objects.equals(suffix, that.suffix)) {
            return false;
        }
        return Objects.equals(comment, that.comment);
    }
    
    @Override
    public int hashCode() {
        int result = placeholderModelContent != null ? placeholderModelContent.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(placeholderSplit);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (placeholder != null ? placeholder.hashCode() : 0);
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "CardItemBean{" +
                       "placeholderModelContent='" + placeholderModelContent + '\'' +
                       ", placeholderSplit=" + Arrays.toString(placeholderSplit) +
                       ", prefix='" + prefix + '\'' +
                       ", placeholder='" + placeholder + '\'' +
                       ", suffix='" + suffix + '\'' +
                       ", comment='" + comment + '\'' +
                       '}';
    }
    
}
