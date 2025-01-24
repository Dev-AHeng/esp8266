package cc.qi7.esp8266_test.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author AHeng
 * @date 2024/08/15 0:08
 */
public class EditPlaceholderFragmentItemBean implements Serializable {
    private static final long serialVersionUID = 2L;
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
    
    /**
     * 显示数据的时间
     */
    private String time;
    
    public EditPlaceholderFragmentItemBean() {
    }
    
    public EditPlaceholderFragmentItemBean(String prefix, String placeholder, String suffix, String comment, String time) {
        this.prefix = prefix;
        this.placeholder = placeholder;
        this.suffix = suffix;
        this.comment = comment;
        this.time = time;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EditPlaceholderFragmentItemBean)) {
            return false;
        }
        
        EditPlaceholderFragmentItemBean bean = (EditPlaceholderFragmentItemBean) o;
        
        if (!Objects.equals(prefix, bean.prefix)) {
            return false;
        }
        if (!Objects.equals(placeholder, bean.placeholder)) {
            return false;
        }
        if (!Objects.equals(suffix, bean.suffix)) {
            return false;
        }
        if (!Objects.equals(comment, bean.comment)) {
            return false;
        }
        return Objects.equals(time, bean.time);
    }
    
    @Override
    public int hashCode() {
        int result = prefix != null ? prefix.hashCode() : 0;
        result = 31 * result + (placeholder != null ? placeholder.hashCode() : 0);
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "\n{\n  \"EditPlaceholderFragmentItemBean\":\n  {\n     " +
                       "\"prefix\": \"" + prefix + '\"' +
                       ",\n     \"placeholder\": \"" + placeholder + '\"' +
                       ",\n     \"suffix\": \"" + suffix + '\"' +
                       ",\n     \"comment\": \"" + comment + '\"' +
                       "\n  }\n}";
    }
}
