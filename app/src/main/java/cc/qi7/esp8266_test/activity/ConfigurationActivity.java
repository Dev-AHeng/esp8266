package cc.qi7.esp8266_test.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Button;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Gravity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.config.SanYuanZhu;

/**
 * 保存三元组配置Activity
 *
 * @author Dev_Heng
 * @date 2024年8月5日022:05:41
 * @status ok
 */
public class ConfigurationActivity extends BaseActivity {
    private UIComponents uiComponents;
    private SharedPreferences sharedPreferences;
    private static final String COPIED_CONFIG_KEY = "copied_config";
    private static final int MENU_COPY = 1;
    private static final int MENU_PASTE = 2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_configuration);
            
            sharedPreferences = getSharedPreferences(SanYuanZhu.SAVE_TABLE_NAME, Context.MODE_PRIVATE);
            
            initViews();
            setupListeners();
            loadSavedData(); // 加载保存的数据
        } catch (Exception e) {
            e.printStackTrace();
            toast("初始化失败: " + e.getMessage());
            finish();
        }
    }
    
    // 使用内部类管理UI组件
    private static class UIComponents {
        TextInputEditText productKey;
        TextInputEditText deviceName;
        TextInputEditText deviceSecret;
        TextInputEditText clientId;
        TextInputEditText publishTopic;
        TextInputEditText subscribeTopic;
        Button saveConfig;
    }
    
    private void initViews() {
        setupToolbar();
        initUIComponents();
    }
    
    private void setupToolbar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());
        
        // 动态加载菜单资源
        // topAppBar.inflateMenu(R.menu.configuration_menu);
        
        // 设置菜单点击事件
        topAppBar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_copy) {
                copyConfigToClipboard();
                return true;
            } else if (itemId == R.id.action_paste) {
                showPasteDialog();
                return true;
            }
            return false;
        });
    }
    
    private void initUIComponents() {
        uiComponents = new UIComponents();
        uiComponents.productKey = findViewById(R.id.productKey);
        uiComponents.deviceName = findViewById(R.id.deviceName);
        uiComponents.deviceSecret = findViewById(R.id.deviceSecret);
        uiComponents.clientId = findViewById(R.id.clientId);
        uiComponents.publishTopic = findViewById(R.id.publishTopic);
        uiComponents.subscribeTopic = findViewById(R.id.subscribeTopic);
        uiComponents.saveConfig = findViewById(R.id.save_config);
    }
    
    private void setupListeners() {
        uiComponents.saveConfig.setOnClickListener(view -> validateAndSave());
    }
    
    private void loadSavedData() {
        // 从SharedPreferences加载保存的数据
        uiComponents.productKey.setText(sharedPreferences.getString(SanYuanZhu.KEY_PRODUCT_KEY, ""));
        uiComponents.deviceName.setText(sharedPreferences.getString(SanYuanZhu.KEY_DEVICE_NAME, ""));
        uiComponents.deviceSecret.setText(sharedPreferences.getString(SanYuanZhu.KEY_DEVICE_SECRET, ""));
        uiComponents.clientId.setText(sharedPreferences.getString(SanYuanZhu.KEY_CLIENT_ID, ""));
        uiComponents.publishTopic.setText(sharedPreferences.getString(SanYuanZhu.KEY_PUBLISH_TOPIC, ""));
        uiComponents.subscribeTopic.setText(sharedPreferences.getString(SanYuanZhu.KEY_SUBSCRIBE_TOPIC, ""));
    }
    
    private void validateAndSave() {
        // 验证所有输入
        if (!validateField(uiComponents.productKey, "ProductKey") ||
                    !validateField(uiComponents.deviceName, "DeviceName") ||
                    !validateField(uiComponents.deviceSecret, "DeviceSecret") ||
                    !validateField(uiComponents.clientId, "ClientId") ||
                    !validateField(uiComponents.publishTopic, "PublishTopic") ||
                    !validateField(uiComponents.subscribeTopic, "SubscribeTopic")) {
            return;
        }
        
        // 获取所有输入值
        String productKeyVal = getTextValue(uiComponents.productKey);
        String deviceNameVal = getTextValue(uiComponents.deviceName);
        String deviceSecretVal = getTextValue(uiComponents.deviceSecret);
        String clientIdVal = getTextValue(uiComponents.clientId);
        String publishTopicVal = getTextValue(uiComponents.publishTopic);
        String subscribeTopicVal = getTextValue(uiComponents.subscribeTopic);
        
        // 保存数据
        saveData(productKeyVal, deviceNameVal, deviceSecretVal,
                clientIdVal, publishTopicVal, subscribeTopicVal);
    }
    
    private boolean validateField(TextInputEditText field, String fieldName) {
        String value = getTextValue(field);
        if (TextUtils.isEmpty(value)) {
            field.setError(fieldName + "不能为空");
            return false;
        }
        return true;
    }
    
    private String getTextValue(TextInputEditText field) {
        return String.valueOf(field.getText()).trim();
    }
    
    private void saveData(String productKeyVal, String deviceNameVal, String deviceSecretVal,
                          String clientIdVal, String publishTopicVal, String subscribeTopicVal) {
        try {
            // 使用事务方式保存所有数据
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SanYuanZhu.KEY_PRODUCT_KEY, productKeyVal);
            editor.putString(SanYuanZhu.KEY_DEVICE_NAME, deviceNameVal);
            editor.putString(SanYuanZhu.KEY_DEVICE_SECRET, deviceSecretVal);
            editor.putString(SanYuanZhu.KEY_CLIENT_ID, clientIdVal);
            editor.putString(SanYuanZhu.KEY_PUBLISH_TOPIC, publishTopicVal);
            editor.putString(SanYuanZhu.KEY_SUBSCRIBE_TOPIC, subscribeTopicVal);
            editor.apply();
            
            // 验证保存是否成功
            verifyDataSaved(productKeyVal);
        } catch (Exception e) {
            e.printStackTrace();
            toast("保存失败: " + e.getMessage());
        }
    }
    
    private void verifyDataSaved(String productKeyVal) {
        String savedProductKey = sharedPreferences.getString(SanYuanZhu.KEY_PRODUCT_KEY, "");
        if (TextUtils.equals(savedProductKey, productKeyVal)) {
            toast("保存成功");
            // 保存成功后关闭界面
            // finish();
        } else {
            toast("保存失败");
        }
    }
    
    private static class ConfigData {
        String productKey;
        String deviceName;
        String deviceSecret;
        String clientId;
        String publishTopic;
        String subscribeTopic;

        // 转换为格式化的JSON，按照界面顺序
        String toJson() {
            try {
                // 使用LinkedHashMap保持插入顺序
                Map<String, Object> orderedMap = new LinkedHashMap<>();
                orderedMap.put("productKey", productKey);
                orderedMap.put("deviceName", deviceName);
                orderedMap.put("deviceSecret", deviceSecret);
                orderedMap.put("clientId", clientId);
                orderedMap.put("publishTopic", publishTopic);
                orderedMap.put("subscribeTopic", subscribeTopic);
                
                // 使用FastJSON的格式化输出
                return JSONObject.toJSONString(
                    orderedMap,
                    true  // 格式化输出
                );
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        // 从JSON恢复
        static ConfigData fromJson(String jsonStr) {
            try {
                ConfigData data = new ConfigData();
                JSONObject json = JSONObject.parseObject(jsonStr);
                data.productKey = json.getString("productKey");
                data.deviceName = json.getString("deviceName");
                data.deviceSecret = json.getString("deviceSecret");
                data.clientId = json.getString("clientId");
                data.publishTopic = json.getString("publishTopic");
                data.subscribeTopic = json.getString("subscribeTopic");
                return data;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    private void copyConfigToClipboard() {
        try {
            ConfigData configData = new ConfigData();
            configData.productKey = getTextValue(uiComponents.productKey);
            configData.deviceName = getTextValue(uiComponents.deviceName);
            configData.deviceSecret = getTextValue(uiComponents.deviceSecret);
            configData.clientId = getTextValue(uiComponents.clientId);
            configData.publishTopic = getTextValue(uiComponents.publishTopic);
            configData.subscribeTopic = getTextValue(uiComponents.subscribeTopic);

            String jsonConfig = configData.toJson();
            if (!TextUtils.isEmpty(jsonConfig)) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("配置", jsonConfig);
                clipboard.setPrimaryClip(clip);
                toast("配置已复制到剪贴板");
            } else {
                toast("复制配置失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast("复制失败: " + e.getMessage());
        }
    }
    
    private void showPasteDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("设置配置");

        // 创建输入框
        TextInputLayout textInputLayout = new TextInputLayout(this);
        TextInputEditText editText = new TextInputEditText(this);
        editText.setHint("请输入JSON配置");
        
        // 设置编辑框的属性
        editText.setMinLines(8);    // 最小显示8行
        editText.setMaxLines(12);   // 最大显示12行
        editText.setGravity(Gravity.TOP);  // 文字从顶部开始
        editText.setVerticalScrollBarEnabled(true);  // 启用垂直滚动条
        
        textInputLayout.addView(editText);
        
        // 设置内边距
        int padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        textInputLayout.setPadding(padding, padding, padding, padding);
        
        builder.setView(textInputLayout);

        builder.setPositiveButton("确定", (dialog, which) -> {
            if (editText == null) {
                toast("输入框异常");
                return;
            }
            
            String jsonStr = editText.getText() != null ? editText.getText().toString().trim() : "";
            
            if (TextUtils.isEmpty(jsonStr)) {
                toast("请输入配置内容");
                return;
            }
            
            // 检查JSON格式是否合法
            try {
                new JSONObject(Boolean.parseBoolean(jsonStr));
            } catch (JSONException e) {
                toast("JSON格式不合法");
                return;
            }
            
            applyJsonConfig(jsonStr);
            
            validateAndSave();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }
    
    private void applyJsonConfig(String jsonStr) {
        try {
            ConfigData configData = ConfigData.fromJson(jsonStr);
            if (configData == null) {
                toast("配置格式错误");
                return;
            }

            // 设置UI值
            uiComponents.productKey.setText(configData.productKey);
            uiComponents.deviceName.setText(configData.deviceName);
            uiComponents.deviceSecret.setText(configData.deviceSecret);
            uiComponents.clientId.setText(configData.clientId);
            uiComponents.publishTopic.setText(configData.publishTopic);
            uiComponents.subscribeTopic.setText(configData.subscribeTopic);

            toast("配置已应用");
        } catch (Exception e) {
            e.printStackTrace();
            toast("设置失败: " + e.getMessage());
        }
    }
}