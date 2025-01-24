package cc.qi7.esp8266_test.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * 日志item的bean
 *
 * @author AHeng
 * @date 2024/08/25 23:42
 */
public class LogItemBean implements Serializable {
    private static final long serialVersionUID = 123L;
    
    /**
     * 发送类型 me 我方
     */
    public static final boolean RIGHT = true;
    
    /**
     * 接收类型 opposite 对方
     */
    public static final boolean LEFT = false;
    
    public static final String APP_SYSTEM_INFO = "APP系统信息";
    public static final String APP = "APP";
    public static final String CLOUD = "云端";
    
    public static Role appRole() {
        return new Role(APP, RIGHT);
    }
    
    public static Role cloudRole() {
        return new Role(CLOUD, LEFT);
    }
    
    public static Role systemRole() {
        return new Role(APP_SYSTEM_INFO, LEFT);
    }
    
    public static class Role implements Serializable {
        private static final long serialVersionUID = 1234L;
        private String name;
        private String avatar;
        private boolean leftOrRight;
        
        public Role() {
        }
        
        public Role(String name, boolean leftOrRight) {
            this.name = name;
            this.leftOrRight = leftOrRight;
        }
        
        public Role(String name, String avatar, boolean leftOrRight) {
            this.name = name;
            this.avatar = avatar;
            this.leftOrRight = leftOrRight;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getAvatar() {
            return avatar;
        }
        
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        
        public boolean getLeftOrRight() {
            return leftOrRight;
        }
        
        public void setLeftOrRight(boolean leftOrRight) {
            this.leftOrRight = leftOrRight;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Role)) {
                return false;
            }
            
            Role role = (Role) o;
            
            if (leftOrRight != role.leftOrRight) {
                return false;
            }
            if (!Objects.equals(name, role.name)) {
                return false;
            }
            return Objects.equals(avatar, role.avatar);
        }
        
        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
            result = 31 * result + (leftOrRight ? 1 : 0);
            return result;
        }
        
        @NonNull
        @Override
        public String toString() {
            return "\n{\n  \"Role\":\n  {\n     " +
                           "\"name\": \"" + name + '\"' +
                           ",\n     \"avatar\": \"" + avatar + '\"' +
                           ",\n     \"leftOrRight\":" + leftOrRight +
                           "\n  }\n}";
        }
    }
    
    /**
     * 类型
     */
    private Role role;
    
    /*
      名字
      private String name;
     */
    
    /**
     * 时间
     */
    private String time;
    
    /**
     * 气泡里的信息
     */
    private String message;
    
    public LogItemBean() {
    
    }
    
    public LogItemBean(Role role, String time, String message) {
        this.role = role;
        this.time = time;
        this.message = message;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogItemBean)) {
            return false;
        }
        
        LogItemBean that = (LogItemBean) o;
        
        if (!Objects.equals(role, that.role)) {
            return false;
        }
        if (!Objects.equals(time, that.time)) {
            return false;
        }
        return Objects.equals(message, that.message);
    }
    
    @Override
    public int hashCode() {
        int result = role != null ? role.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
    
    @NonNull
    @Override
    public String toString() {
        return "\n{\n  \"LogItemBean\":\n  {\n     " +
                       "\"role\":" + role +
                       ",\n     \"time\": \"" + time + '\"' +
                       ",\n     \"message\": \"" + message + '\"' +
                       "\n  }\n}";
    }
}
