package cc.qi7.esp8266_test.bean;

import androidx.annotation.NonNull;

/**
 * @author AHeng
 * @date 2024/08/05 19:18
 */
public class GridViewItemBean {
    /**
     * 类型 Button/Switch
     */
    private String widgetType;
    /**
     * 控件文本
     */
    private String widgetText;
    /**
     * 按钮指令
     */
    private String buttonInstruction;
    /**
     * 开关的开指令
     */
    private String switchOnInstruction;
    /**
     * 开关的关 指令
     */
    private String switchOffInstruction;
    /**
     * 开关状态
     */
    private boolean witchStatus;
    
    public GridViewItemBean(String widgetType, String widgetText, String buttonInstruction, String switchOnInstruction, String switchOffInstruction, boolean witchStatus) {
        this.widgetType = widgetType;
        this.widgetText = widgetText;
        this.buttonInstruction = buttonInstruction;
        this.switchOnInstruction = switchOnInstruction;
        this.switchOffInstruction = switchOffInstruction;
        this.witchStatus = witchStatus;
    }
    
    public String getWidgetType() {
        return widgetType;
    }
    
    public void setWidgetType(String widgetType) {
        this.widgetType = widgetType;
    }
    
    public String getWidgetText() {
        return widgetText;
    }
    
    public void setWidgetText(String widgetText) {
        this.widgetText = widgetText;
    }
    
    public String getButtonInstruction() {
        return buttonInstruction;
    }
    
    public void setButtonInstruction(String buttonInstruction) {
        this.buttonInstruction = buttonInstruction;
    }
    
    public boolean getWitchStatus() {
        return witchStatus;
    }
    
    public void setWitchStatus(boolean witchStatus) {
        this.witchStatus = witchStatus;
    }
    
    public String getSwitchOnInstruction() {
        return switchOnInstruction;
    }
    
    public void setSwitchOnInstruction(String switchOnInstruction) {
        this.switchOnInstruction = switchOnInstruction;
    }
    
    public String getSwitchOffInstruction() {
        return switchOffInstruction;
    }
    
    public void setSwitchOffInstruction(String switchOffInstruction) {
        this.switchOffInstruction = switchOffInstruction;
    }
    
    public String toStr() {
        return "{" +
                       "'widgetType':'" + widgetType + '\'' +
                       ", 'widgetText':'" + widgetText + '\'' +
                       ", 'buttonInstruction':'" + buttonInstruction + '\'' +
                       ", 'switchOnInstruction':'" + switchOnInstruction + '\'' +
                       ", 'switchOffInstruction':'" + switchOffInstruction + '\'' +
                       ", 'witchStatus':" + witchStatus +
                       '}';
    }
    
    @NonNull
    @Override
    public String toString() {
        return "{" +
                       "'widgetType':'" + widgetType + '\'' +
                       ", 'widgetText':'" + widgetText + '\'' +
                       ", 'buttonInstruction':'" + buttonInstruction + '\'' +
                       ", 'switchOnInstruction':'" + switchOnInstruction + '\'' +
                       ", 'switchOffInstruction':'" + switchOffInstruction + '\'' +
                       ", 'witchStatus':" + witchStatus +
                       '}';
    }
}
