package cc.qi7.esp8266_test.config;

/**
 * @author AHeng
 * @date 2024/09/25 11:11
 */
public class Settings {
    
    /**
     * 存储SharedPreferences的表名
     */
    public static final String SAVE_TABLE_NAME = "card_text_size";
    
    /**
     * 定义SharedPreferences的键
     */
    public static final String KEY_GRID_COLUMN = "gridColumn";
    public static final String KEY_PREFIX_TEXT_SIZE = "defaultPrefixTextSize";
    public static final String KEY_PLACEHOLDER_TEXT_SIZE = "defaultDefaultPlaceholderTextSize";
    public static final String KEY_SUFFIX_TEXT_SIZE = "defaultSuffixTextSize";
    public static final String KEY_TIME_TEXT_SIZE = "defaultTimeTextSize";
    
    /**
     * 默认
     */
    public static final String KEY_DEFAULT_GRID_COLUMN = "defaultGridColumn";
    public static final String KEY_DEFAULT_PREFIX_TEXT_SIZE = "defaultPrefixTextSize";
    public static final String KEY_DEFAULT_PLACEHOLDER_TEXT_SIZE = "defaultDefaultPlaceholderTextSize";
    public static final String KEY_DEFAULT_SUFFIX_TEXT_SIZE = "defaultSuffixTextSize";
    public static final String KEY_DEFAULT_TIME_TEXT_SIZE = "defaultTimeTextSize";
    
    /**
     * 默认值
     */
    public static final float DEFAULT_GRID_COLUMN = 2.0f;
    public static final float DEFAULT_PREFIX_TEXT_SIZE = 48.0f;
    public static final float DEFAULT_PLACEHOLDER_TEXT_SIZE = 128.0f;
    public static final float DEFAULT_SUFFIX_TEXT_SIZE = 48.0f;
    public static final float DEFAULT_TIME_TEXT_SIZE = 32.0f;
    
}
