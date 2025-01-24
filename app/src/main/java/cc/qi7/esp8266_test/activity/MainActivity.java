package cc.qi7.esp8266_test.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.collection.ArrayMap;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.dm.api.InitResult;
import com.aliyun.alink.dm.api.IoTApiClientConfig;
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener;
import com.aliyun.alink.linkkit.api.IoTDMConfig;
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linkkit.api.LinkKitInitParams;
import com.aliyun.alink.linksdk.channel.core.persistent.PersistentNet;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttPublishRequest;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectUnscribeListener;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tools.AError;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.adapter.InstructionGridAdapter;
import cc.qi7.esp8266_test.adapter.LogRecyclerViewAsyncDiffAdapter;
import cc.qi7.esp8266_test.adapter.MainActivityRealTimeRecycleViewAdapter;
import cc.qi7.esp8266_test.bean.CardGroupBean;
import cc.qi7.esp8266_test.bean.EditPlaceholderFragmentItemBean;
import cc.qi7.esp8266_test.bean.GridViewItemBean;
import cc.qi7.esp8266_test.bean.LogItemBean;
import cc.qi7.esp8266_test.config.CreateInstructionWidget;
import cc.qi7.esp8266_test.config.PublicConfig;
import cc.qi7.esp8266_test.config.SanYuanZhu;
import cc.qi7.esp8266_test.config.Settings;
import cc.qi7.esp8266_test.utils.AppUtils;
import cc.qi7.esp8266_test.utils.BaseUtils;
import cc.qi7.esp8266_test.utils.DateTimeUtil;
import cc.qi7.esp8266_test.utils.DebounceUtils;

/**
 * @author Dev_Heng
 * @date 2024年8月5日01:2:49
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "日志";
    
    private LinearLayoutCompat notInstructionLinearLayoutCompat;
    
    private InstructionGridAdapter instructionGridAdapter;
    
    private SharedPreferences sanYuanSharedPreferences;
    private SharedPreferences sharedPreferences;
    private SharedPreferences instructionSharedPreferences;
    private Gson gson;
    
    private TextInputLayout widgetTypeTextInputLayout, widgetNameTextInputLayout, buttonInstructionTextInputLayout, switchOnInstructionTextInputLayout, switchOffInstructionTextInputLayout;
    private ActivityResultLauncher<Intent> someResultLauncher;
    private ActivityResultLauncher<Intent> settingsResultLauncher;
    private MainActivityRealTimeRecycleViewAdapter activityRealTimeRecycleViewAdapter;
    
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    // 两次点击的时间间隔，单位毫秒
    private static final long DOUBLE_CLICK_TIME_INTERVAL = 800;
    private long firstClickTime = 0;
    private MaterialToolbar topAppBar;
    
    // 公共日志List
    private final List<LogItemBean> publicLogItemBeans = new ArrayList<>();
    private LogRecyclerViewAsyncDiffAdapter logRecyclerViewAsyncDiffAdapter;
    private RecyclerView showRealTimeDataRecyclerView;
    
    float gridColumn;
    float prefixTextSize;
    float placeholderTextSize;
    float suffixTextSize;
    float timeTextSize;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        firstRun();
        
        toolBarPart();
        
        drawerPart();
        
        connectPart();
        
        instructionPart();
        
        realTimePart();
        
        settingsPart();
        
        // test(100L, retuenString -> {
        //     activityRealTimeRecycleViewAdapter.modifyCard(retuenString);
        // });
    }
    
    /**
     * 首次运行
     */
    private void firstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstRun", Context.MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        
        if (isFirstRun) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstRun", false);
            // 使用apply()替代commit()以提高性能
            editor.apply();
            
            toast("首次运行应用");
            
            saveDefaultConfig();
            
            saveDefaultInstruction();
            
            saveDefaultRealTime();
        }
    }
    
    /**
     * 保存默认配置
     */
    private void saveDefaultConfig() {
        SharedPreferences config = getSharedPreferences(SanYuanZhu.SAVE_TABLE_NAME, Context.MODE_PRIVATE);
        try {
            // 使用Map存储要保存的键值对
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(SanYuanZhu.KEY_PRODUCT_KEY, "k1mfoT3OGs7");
            dataMap.put(SanYuanZhu.KEY_DEVICE_NAME, "fourApp");
            dataMap.put(SanYuanZhu.KEY_DEVICE_SECRET, "c05b9685b0a75ca9e5daa21d545b59ec");
            dataMap.put(SanYuanZhu.KEY_CLIENT_ID, "iot-06z00bbpww7a03a.mqtt.iothub.aliyuncs.com:1883");
            dataMap.put(SanYuanZhu.KEY_PUBLISH_TOPIC, "/k1mfoT3OGs7/fourApp/user/fourApp");
            dataMap.put(SanYuanZhu.KEY_SUBSCRIBE_TOPIC, "/k1mfoT3OGs7/fourApp/user/get");
            
            // 使用事务方式批量保存数据
            SharedPreferences.Editor editor = config.edit();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                editor.putString(entry.getKey(), entry.getValue());
            }
            editor.apply();
            
            // 验证保存是否成功
            String savedProductKey = config.getString(SanYuanZhu.KEY_PRODUCT_KEY, "");
            if (TextUtils.equals(savedProductKey, "k1mfoT3OGs7")) {
                toast("保存成功");
            } else {
                toast("保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "保存失败: " + e.getMessage());
            toast("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存默认指令
     */
    private void saveDefaultInstruction() {
        List<GridViewItemBean> aa = new ArrayList<>();
        aa.add(new GridViewItemBean("button", "开始检测心率血氧", "{\"@1@<>\":1}", "", "", false));
        aa.add(new GridViewItemBean("button", "结束检测心率血氧", "{\"@0@<>\":1}", "", "", false));
        aa.add(new GridViewItemBean("button", "开始测血压", "{\"*1*<>\":1}", "", "", false));
        aa.add(new GridViewItemBean("button", "结束测血压", "{\"*0*<>\":1}", "", "", false));
        aa.add(new GridViewItemBean("button", "开始测温度", "{\"&1&<>\":1}", "", "", false));
        aa.add(new GridViewItemBean("button", "结束测温度", "{\"&0&<>\":1}", "", "", false));
        
        SharedPreferences instructionSharedPreferences = getSharedPreferences(CreateInstructionWidget.SAVE_TABLE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = instructionSharedPreferences.edit();
        editor.putString(CreateInstructionWidget.KEY_WIDGET_JSON, new Gson().toJson(aa));
        editor.apply();
    }
    
    /**
     * 保存默认试试卡片
     */
    private void saveDefaultRealTime() {
        List<CardGroupBean> cardGroupBeans = new CopyOnWriteArrayList<>();
        
        CardGroupBean cardGroupBean = new CardGroupBean();
        cardGroupBean.setPlaceholderModelContent("{\"params\":{\"TE\":@1@}}");
        String[] strings = {"{\"params\":{\"TE\":", "}}"};
        cardGroupBean.setPlaceholderSplit(strings);
        Map<String, EditPlaceholderFragmentItemBean> map = new LinkedHashMap<>();
        map.put("@1@", new EditPlaceholderFragmentItemBean("温度", "--", "℃", "温度", DateTimeUtil.getCurrentFormattedTime()));
        cardGroupBean.setCardItemBeanMap(map);
        cardGroupBeans.add(cardGroupBean);
        
        CardGroupBean cardGroupBean1 = new CardGroupBean();
        cardGroupBean1.setPlaceholderModelContent("{\"params\":{\"BP\":@1@}}");
        String[] strings1 = {"{\"params\":{\"BP\":", "}}"};
        cardGroupBean1.setPlaceholderSplit(strings1);
        Map<String, EditPlaceholderFragmentItemBean> map1 = new LinkedHashMap<>();
        map1.put("@1@", new EditPlaceholderFragmentItemBean("血压", "--", "mmHg", "血压", DateTimeUtil.getCurrentFormattedTime()));
        cardGroupBean1.setCardItemBeanMap(map1);
        cardGroupBeans.add(cardGroupBean1);
        
        CardGroupBean cardGroupBean2 = new CardGroupBean();
        cardGroupBean2.setPlaceholderModelContent("{\"params\":{\"HR\":@1@}}");
        String[] strings2 = {"{\"params\":{\"HR\":", "}}"};
        cardGroupBean2.setPlaceholderSplit(strings2);
        Map<String, EditPlaceholderFragmentItemBean> map2 = new LinkedHashMap<>();
        map2.put("@1@", new EditPlaceholderFragmentItemBean("心率", "--", "", "心率", DateTimeUtil.getCurrentFormattedTime()));
        cardGroupBean2.setCardItemBeanMap(map2);
        cardGroupBeans.add(cardGroupBean2);
        
        CardGroupBean cardGroupBean3 = new CardGroupBean();
        cardGroupBean3.setPlaceholderModelContent("{\"params\":{\"SPO2\":@1@}}");
        String[] strings3 = {"{\"params\":{\"SPO2\":", "}}"};
        cardGroupBean3.setPlaceholderSplit(strings3);
        Map<String, EditPlaceholderFragmentItemBean> map3 = new LinkedHashMap<>();
        map3.put("@1@", new EditPlaceholderFragmentItemBean("血氧", "--", "", "血氧", DateTimeUtil.getCurrentFormattedTime()));
        cardGroupBean3.setCardItemBeanMap(map3);
        cardGroupBeans.add(cardGroupBean3);
        
        log("哈哈" + cardGroupBeans);
        
        try (FileOutputStream fos = openFileOutput(PublicConfig.CARD_GROUP_BEAN, MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(cardGroupBeans);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void settingsPart() {
        settingsResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    // 获取返回的putExtra
                    gridColumn = data.getFloatExtra(Settings.KEY_GRID_COLUMN, Settings.DEFAULT_GRID_COLUMN);
                    prefixTextSize = data.getFloatExtra(Settings.KEY_PREFIX_TEXT_SIZE, Settings.DEFAULT_PREFIX_TEXT_SIZE);
                    placeholderTextSize = data.getFloatExtra(Settings.KEY_PLACEHOLDER_TEXT_SIZE, Settings.DEFAULT_PLACEHOLDER_TEXT_SIZE);
                    suffixTextSize = data.getFloatExtra(Settings.KEY_SUFFIX_TEXT_SIZE, Settings.DEFAULT_SUFFIX_TEXT_SIZE);
                    timeTextSize = data.getFloatExtra(Settings.KEY_TIME_TEXT_SIZE, Settings.DEFAULT_TIME_TEXT_SIZE);
                    
                    setCardTextSize(gridColumn, prefixTextSize, placeholderTextSize, suffixTextSize, timeTextSize);
                }
            }
        });
    }
    
    private void drawerPart() {
        drawerLayout = findViewById(R.id.drawerLayout);
        
        // 创建 ActionBarDrawerToggle 实例
        toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // 设置 DrawerLayout 的 DrawerListener
        drawerLayout.addDrawerListener(toggle);
        // 同步状态
        toggle.syncState();
        // 调用 getSupportActionBar().setDisplayHomeAsUpEnabled(true) 使返回按钮可用
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        
        RecyclerView showLogRecyclerView = findViewById(R.id.showLogRecyclerView);
        showLogRecyclerView.setItemAnimator(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // 从底部开始显示
        // linearLayoutManager.setStackFromEnd(true);
        showLogRecyclerView.setLayoutManager(linearLayoutManager);
        logRecyclerViewAsyncDiffAdapter = new LogRecyclerViewAsyncDiffAdapter();
        showLogRecyclerView.setAdapter(logRecyclerViewAsyncDiffAdapter);
        
        showLogRecyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                showLogRecyclerView.post(() -> {
                    if (logRecyclerViewAsyncDiffAdapter.getItemCount() > 0) {
                        showLogRecyclerView.smoothScrollToPosition(logRecyclerViewAsyncDiffAdapter.getItemCount() - 1);
                    }
                });
            }
        });
        
        // 监听抽屉状态变化
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // 抽屉视图正在滑动时调用
            }
            
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // 抽屉视图完全打开时调用, 滚动到最后
                showLogRecyclerView.scrollToPosition(logRecyclerViewAsyncDiffAdapter.getItemCount() - 1);
            }
            
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // 抽屉视图完全关闭时调用
            }
            
            @Override
            public void onDrawerStateChanged(int newState) {
                // 抽屉视图状态改变时调用
            }
        });
        
        
        AppCompatEditText instructionEditText = findViewById(R.id.instructionEditText);
        AppCompatButton sendInstructionButton = findViewById(R.id.sendInstructionButton);
        // 发送命令
        sendInstructionButton.setOnClickListener(v -> {
            String instruction = Objects.requireNonNull(instructionEditText.getText()).toString().trim();
            if (TextUtils.isEmpty(instruction)) {
                Toast.makeText(MainActivity.this, "指令不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(instruction);
            instructionEditText.setText("");
            // 此句为设置显示
            showLogRecyclerView.scrollToPosition(logRecyclerViewAsyncDiffAdapter.getItemCount() - 1);
        });
        
        addLog("日志在这里显示哦\uD83D\uDE18\uD83D\uDE18", LogItemBean.systemRole());
    }
    
    
    // @SuppressLint("DefaultLocale")
    // private void test(long threadSleepTime, Consumer<String> consumer) {
    //     new Thread(() -> {
    //         while (true) {
    //
    //             // @1@
    //             List<String> list = new ArrayList<>();
    //             // list.add(String.format("{\"@%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"params\":{\"SmokeSensor\":%d}}", RandomUtil.getRandomInt(1, 100)));
    //
    //             list.add(String.format("{\"&%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"$%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"#%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"*%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"?%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"!%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"+%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             list.add(String.format("{\"-%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //
    // /*
    // {"@@1@": 0}
    //                 {"&@1@": 0}
    //                 {"$@1@": 0}
    //                 {"#@1@": 0}
    //                 {"*@1@": 0}
    //                 {"?@1@": 0}
    //                 {"!@1@": 0}
    //                 {"+@1@": 0}
    //                 {"-@1@": 0}
    //                 */
    //             // list.add(String.format("{\"343%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             // list.add(String.format("{\"@%d\": 3345}", RandomUtil.getRandomInt(1, 100)));
    //             // list.add(String.format("{\"5%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             // list.add(String.format("{\"是多少%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             // list.add(String.format("{\"sr%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //             // list.add(String.format("{\"_%d\": 0}", RandomUtil.getRandomInt(1, 100)));
    //
    //             // if (isShow) {
    //             //
    //             // }
    //             //
    //             // log(isShow);
    //
    //             String randomElement = RandomUtil.getRandomElement(list);
    //             consumer.accept(randomElement);
    //
    //             try {
    //                 Thread.sleep(threadSleepTime);
    //             } catch (InterruptedException e) {
    //                 throw new RuntimeException(e);
    //             }
    //         }
    //     }).start();
    // }
    //
    
    /**
     * 顶部工具栏部分
     */
    private void toolBarPart() {
        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        
        // topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
        //     @SuppressLint("NonConstantResourceId")
        //     @Override
        //     public boolean onMenuItemClick(MenuItem item) {
        //         switch (item.getItemId()) {
        //             case R.id.getConfig:
        //                 goToActivity(ConfigurationActivity.class);
        //                 return true;
        //
        //             case R.id.about:
        //                 toast("关于");
        //                 return true;
        //
        //             default:
        //                 return false;
        //         }
        //     }
        // });
        
        // 设置 onBackPressedDispatcher
        // getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
        //     @Override
        //     public void handleOnBackPressed() {
        //         // 如果 Drawer 是打开的，则先关闭 Drawer
        //         if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        //             drawerLayout.closeDrawer(GravityCompat.START);
        //         } else {
        //             // Drawer 关闭时执行默认的返回行为
        //             onBackPressed();
        //         }
        //     }
        // });
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载菜单资源文件
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }
    
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.getConfig) {
            goToActivity(ConfigurationActivity.class);
            return true;
        }
        
        if (itemId == R.id.settings) {
            // 检查是否有实时数据卡片
            if (activityRealTimeRecycleViewAdapter == null || activityRealTimeRecycleViewAdapter.getItemCount() == 0) {
                MaterialAlertDialogBuilder mdDialog = new MaterialAlertDialogBuilder(this);
                mdDialog.setIcon(R.drawable.baseline_error_24);
                mdDialog.setTitle("提示");
                mdDialog.setMessage("请先添加实时数据卡片后再进行设置");
                mdDialog.setPositiveButton("确定", null);
                mdDialog.show();
                return true;
            }
            Intent intent = new Intent(this, SettingsActivity.class);
            settingsResultLauncher.launch(intent);
            return true;
        }
        
        if (itemId == R.id.clearLog) {
            publicLogItemBeans.clear();
            addLog("日志在这里显示哦\uD83D\uDE18\uD83D\uDE18", LogItemBean.systemRole());
            toast("清除成功");
            return true;
        }
        
        if (itemId == R.id.about) {
            MaterialAlertDialogBuilder aboutDialog = new MaterialAlertDialogBuilder(this);
            aboutDialog.setTitle("关于");
            aboutDialog.setMessage("物联网\nRuntime version: 1.0.0\nBuilt on 2024.8.28\nBy AHeng");
            aboutDialog.setNegativeButton("取消", null);
            aboutDialog.setPositiveButton("联系作者QQ", (dialogInterface, i) -> {
                if (AppUtils.isAppInstalled(this, "com.tencent.mobileqq")) {
                    // 跳转到QQ
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=1919196455")));
                } else {
                    Toast.makeText(this, "未安装QQ", Toast.LENGTH_SHORT).show();
                }
            });
            aboutDialog.show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 调用此方法以确保正确显示或隐藏抽屉图标
        toggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 调用此方法以确保在配置更改后正确显示或隐藏抽屉图标
        toggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onBackPressed() {
        // 如果 Drawer 是打开的，则先关闭 Drawer
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            long secondClickTime = System.currentTimeMillis();
            if (secondClickTime - firstClickTime > DOUBLE_CLICK_TIME_INTERVAL) {
                // 如果两次点击的时间间隔超过了设定的阈值，则重置firstClickTime
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                firstClickTime = secondClickTime;
            } else {
                saveInstructionData();
                super.onBackPressed();
                PersistentNet.getInstance().destroy();
                activityRealTimeRecycleViewAdapter.saveToLocal();
                System.exit(0);
            }
        }
    }
    
    /**
     * 连接和取消连接部分
     */
    private void connectPart() {
        // 连接
        MaterialButton connect = findViewById(R.id.connect);
        connect.setOnClickListener(view -> {
            if (DebounceUtils.isDebounce()) {
                return;
            }
            
            sanYuanSharedPreferences = getSharedPreferences(SanYuanZhu.SAVE_TABLE_NAME, Context.MODE_PRIVATE);
            String spProductKey = sanYuanSharedPreferences.getString(SanYuanZhu.KEY_PRODUCT_KEY, null);
            String spDeviceName = sanYuanSharedPreferences.getString(SanYuanZhu.KEY_DEVICE_NAME, null);
            String spDeviceSecret = sanYuanSharedPreferences.getString(SanYuanZhu.KEY_DEVICE_SECRET, null);
            String spClientId = sanYuanSharedPreferences.getString(SanYuanZhu.KEY_CLIENT_ID, null);
            String spSubscribeTopic = sanYuanSharedPreferences.getString(SanYuanZhu.KEY_SUBSCRIBE_TOPIC, null);
            
            if (TextUtils.isEmpty(spProductKey) || TextUtils.isEmpty(spDeviceName) || TextUtils.isEmpty(spDeviceSecret) || TextUtils.isEmpty(spClientId) || TextUtils.isEmpty(spSubscribeTopic)) {
                MaterialAlertDialogBuilder mdDialog = new MaterialAlertDialogBuilder(this);
                mdDialog.setIcon(R.drawable.baseline_error_24);
                mdDialog.setTitle("提示");
                mdDialog.setMessage("你还没配置阿里云物联网平台硬件设备的三元组");
                mdDialog.setNegativeButton("取消", null);
                mdDialog.setPositiveButton("去配置", (dialogInterface, i) -> {
                    goToActivity(ConfigurationActivity.class);
                });
                mdDialog.show();
                return;
            }
            
            // 初始化连接
            initMqttManager(spProductKey, spDeviceName, spDeviceSecret, spClientId, spSubscribeTopic);
        });
        
        // 断开连接
        MaterialButton disconnect = findViewById(R.id.disconnect);
        disconnect.setOnClickListener(view -> {
            if (DebounceUtils.isDebounce()) {
                return;
            }
            PersistentNet.getInstance().destroy();
            
            addLog("已断开连接", LogItemBean.systemRole());
        });
        
    }
    
    /**
     * 添加日志
     *
     * @param message 信息
     * @param role    我或者对方
     */
    private void addLog(String message, LogItemBean.Role role) {
        publicLogItemBeans.add(new LogItemBean(role, DateTimeUtil.getCurrentFormattedTime(), message));
        logRecyclerViewAsyncDiffAdapter.submitList(publicLogItemBeans);
    }
    
    /**
     * 指令部分
     */
    private void instructionPart() {
        GridView gridView = findViewById(R.id.gridview);
        gridView.setNumColumns(2);
        
        notInstructionLinearLayoutCompat = findViewById(R.id.notInstructionLinearLayoutCompat);
        
        List<GridViewItemBean> gridViewItemModels = new ArrayList<>();
        
        instructionSharedPreferences = getSharedPreferences(CreateInstructionWidget.SAVE_TABLE_NAME, Context.MODE_PRIVATE);
        
        gson = new GsonBuilder()
                       .disableHtmlEscaping()
                       .serializeNulls()
                       .create();
        
        // 获取本地数据
        String widgetJson = instructionSharedPreferences.getString(CreateInstructionWidget.KEY_WIDGET_JSON, "");
        if (TextUtils.isEmpty(widgetJson) || "[]".equals(widgetJson) || widgetJson.length() <= 2) {
            // 显示没有数据提示
            notInstructionLinearLayoutCompat.setVisibility(View.VISIBLE);
        } else {
            Type listType = new TypeToken<ArrayList<GridViewItemBean>>() {
            }.getType();
            List<GridViewItemBean> gridViewItemModelJson = gson.fromJson(widgetJson, listType);
            gridViewItemModels.addAll(gridViewItemModelJson);
        }
        
        // 添加指令
        MaterialButton button = findViewById(R.id.addInstruction);
        button.setOnClickListener(v -> {
            if (DebounceUtils.isDebounce()) {
                return;
            }
            addInstruction();
        });
        
        // 创建适配器并设置给GridView
        instructionGridAdapter = new InstructionGridAdapter(this, gridViewItemModels);
        gridView.setAdapter(instructionGridAdapter);
        
        // gridView内部控件回调
        instructionGridAdapter.setInstructionEventListener(new InstructionGridAdapter.InstructionEventListener() {
            @Override
            public void onSwitchLongClick(InstructionGridAdapter instructionGridAdapter, int position) {
                if (DebounceUtils.isDebounce()) {
                    return;
                }
                modifyInstruction(position, instructionGridAdapter);
            }
            
            @Override
            public void onSwitchClick(GridViewItemBean gridViewItemBean, boolean isChecked) {
                if (DebounceUtils.isDebounce()) {
                    return;
                }
                
                log(gridViewItemBean.toStr());
                
                if (gridViewItemBean.getWitchStatus()) {
                    toast("开", gridViewItemBean.getSwitchOnInstruction());
                    sendMessage(gridViewItemBean.getSwitchOnInstruction());
                } else {
                    toast("关", gridViewItemBean.getSwitchOffInstruction());
                    sendMessage(gridViewItemBean.getSwitchOffInstruction());
                }
                
            }
            
            @Override
            public void onButtonLongClick(InstructionGridAdapter instructionGridAdapter, int position) {
                if (DebounceUtils.isDebounce()) {
                    return;
                }
                toast("长按");
                modifyInstruction(position, instructionGridAdapter);
            }
            
            @Override
            public void onButtonClick(GridViewItemBean gridViewItemBean, View widgetView) {
                if (DebounceUtils.isDebounce()) {
                    return;
                }
                
                toast(gridViewItemBean.getButtonInstruction());
                
                sendMessage(gridViewItemBean.getButtonInstruction());
                log(gridViewItemBean.toStr());
            }
        });
        
    }
    
    
    /**
     * 实时数据部分
     */
    private void realTimePart() {
        LinearLayoutCompat notRealTimeCardLinearLayoutCompat = findViewById(R.id.notRealTimeCardLinearLayoutCompat);
        showRealTimeDataRecyclerView = findViewById(R.id.showRealTimeDataRecyclerView);
        showRealTimeDataRecyclerView.setItemAnimator(null);
        
        activityRealTimeRecycleViewAdapter = new MainActivityRealTimeRecycleViewAdapter(this, notRealTimeCardLinearLayoutCompat);
        showRealTimeDataRecyclerView.setAdapter(activityRealTimeRecycleViewAdapter);
        
        // 从SharedPreferences加载设置
        sharedPreferences = getSharedPreferences(Settings.SAVE_TABLE_NAME, Context.MODE_PRIVATE);
        gridColumn = sharedPreferences.getFloat(Settings.KEY_GRID_COLUMN, Settings.DEFAULT_GRID_COLUMN);
        prefixTextSize = sharedPreferences.getFloat(Settings.KEY_PREFIX_TEXT_SIZE, Settings.DEFAULT_PREFIX_TEXT_SIZE);
        placeholderTextSize = sharedPreferences.getFloat(Settings.KEY_PLACEHOLDER_TEXT_SIZE, Settings.DEFAULT_PLACEHOLDER_TEXT_SIZE);
        suffixTextSize = sharedPreferences.getFloat(Settings.KEY_SUFFIX_TEXT_SIZE, Settings.DEFAULT_SUFFIX_TEXT_SIZE);
        timeTextSize = sharedPreferences.getFloat(Settings.KEY_TIME_TEXT_SIZE, Settings.DEFAULT_TIME_TEXT_SIZE);
        
        // 应用设置
        setCardTextSize(gridColumn, prefixTextSize, placeholderTextSize, suffixTextSize, timeTextSize);
        
        // 添加实时数据
        MaterialButton addRealTimeDataBtn = findViewById(R.id.addRealTimeData);
        addRealTimeDataBtn.setOnClickListener(v -> {
            if (DebounceUtils.isDebounce()) {
                return;
            }
            
            Intent intent = new Intent(this, CreateRealTimeCardActivity.class);
            // 可以添加需要的extras
            someResultLauncher.launch(intent);
        });
        
        // 使用ActivityResultContracts.StartActivityForResult来启动Activity B
        someResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // 从Intent中获取数据
                Intent data = result.getData();
                if (data != null) {
                    CardGroupBean cardGroupBean = (CardGroupBean) data.getSerializableExtra(PublicConfig.SERIALIZABLE_EXTRA_KEY);
                    
                    activityRealTimeRecycleViewAdapter.addCard(cardGroupBean);
                }
            }
        });
    }
    
    /**
     * 设置滑动条值
     *
     * @param gridColumn          卡片列数
     * @param prefixTextSize      前缀
     * @param placeholderTextSize 占位符
     * @param suffixTextSize      后缀
     * @param timeTextSize        时间
     */
    public void setCardTextSize(float gridColumn, float prefixTextSize, float placeholderTextSize, float suffixTextSize, float timeTextSize) {
        showRealTimeDataRecyclerView.setLayoutManager(new GridLayoutManager(this, (int) gridColumn));
        activityRealTimeRecycleViewAdapter.setCardItemTextSizeBean(prefixTextSize, placeholderTextSize, suffixTextSize, timeTextSize);
    }
    
    private View initAddInstructionDialogView() {
        View customView = getLayoutInflater().inflate(R.layout.dialog_add_instruction, null);
        // 选择控件类型
        widgetTypeTextInputLayout = customView.findViewById(R.id.widgetTypeTextInputLayout);
        // 控件名称
        widgetNameTextInputLayout = customView.findViewById(R.id.widgetNameTextInputLayout);
        // 按钮指令
        buttonInstructionTextInputLayout = customView.findViewById(R.id.buttonInstructionTextInputLayout);
        // 开关指令 开
        switchOnInstructionTextInputLayout = customView.findViewById(R.id.onSwitchInstructionTextInputLayout);
        // 开关指令 关
        switchOffInstructionTextInputLayout = customView.findViewById(R.id.offSwitchInstructionTextInputLayout);
        buttonInstructionTextInputLayout.setVisibility(View.GONE);
        switchOnInstructionTextInputLayout.setVisibility(View.GONE);
        switchOffInstructionTextInputLayout.setVisibility(View.GONE);
        return customView;
    }
    
    private void addInstruction() {
        View customView = initAddInstructionDialogView();
        
        // widgetTypeTextInputLayout --> MaterialAutoCompleteTextView
        MaterialAutoCompleteTextView widgetTypeMaterialAutoCompleteTextView = customView.findViewById(R.id.widgetTypeMaterialAutoCompleteTextView);
        widgetTypeMaterialAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedValue = String.valueOf(parent.getItemAtPosition(position));
            if (selectedValue.toLowerCase().contains("button")) {
                toast(selectedValue);
                buttonInstructionTextInputLayout.setVisibility(View.VISIBLE);
                switchOnInstructionTextInputLayout.setVisibility(View.GONE);
                switchOffInstructionTextInputLayout.setVisibility(View.GONE);
                return;
            }
            
            if (selectedValue.toLowerCase().contains("switch")) {
                buttonInstructionTextInputLayout.setVisibility(View.GONE);
                switchOnInstructionTextInputLayout.setVisibility(View.VISIBLE);
                switchOffInstructionTextInputLayout.setVisibility(View.VISIBLE);
            }
        });
        
        // 添加指令控件弹窗
        MaterialAlertDialogBuilder mdDialog = new MaterialAlertDialogBuilder(this);
        // mdDialog.setIcon(R.drawable.baseline_add_box_24);
        mdDialog.setCancelable(false);
        mdDialog.setTitle("添加指令");
        mdDialog.setView(customView);
        mdDialog.setNegativeButton("取消", null);
        mdDialog.setPositiveButton("添加", null);
        // 用于禁止点击添加后自动关闭弹窗
        AlertDialog alertDialog = mdDialog.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            if (DebounceUtils.isDebounce()) {
                return;
            }
            // 清除控件错误
            widgetTypeTextInputLayout.setError(null);
            widgetNameTextInputLayout.setError(null);
            buttonInstructionTextInputLayout.setError(null);
            switchOnInstructionTextInputLayout.setError(null);
            switchOffInstructionTextInputLayout.setError(null);
            
            String widgetTypeTextInputLayoutContent = Objects.requireNonNull(widgetTypeTextInputLayout.getEditText()).getText().toString();
            String widgetNameTextInputLayoutContent = Objects.requireNonNull(widgetNameTextInputLayout.getEditText()).getText().toString();
            String buttonInstructionTextInputLayoutContent = Objects.requireNonNull(buttonInstructionTextInputLayout.getEditText()).getText().toString();
            String switchOnInstructionTextInputLayoutContent = Objects.requireNonNull(switchOnInstructionTextInputLayout.getEditText()).getText().toString();
            String switchOffInstructionTextInputLayoutContent = Objects.requireNonNull(switchOffInstructionTextInputLayout.getEditText()).getText().toString();
            
            // 一个一个判断, 然后错误的setError
            if (widgetTypeTextInputLayoutContent.isEmpty()) {
                widgetTypeTextInputLayout.setError("控件类型不能为空");
                return;
            }
            
            if (widgetNameTextInputLayoutContent.isEmpty()) {
                widgetNameTextInputLayout.setError("控件名称不能为空");
                return;
            }
            
            if (widgetNameTextInputLayoutContent.length() > 10) {
                widgetNameTextInputLayout.setError("控件名称不能超过10个字符");
                return;
            }
            
            String widgetTypeLowerCase = widgetTypeTextInputLayoutContent.toLowerCase();
            if (widgetTypeLowerCase.contains("button")) {
                if (buttonInstructionTextInputLayoutContent.isEmpty()) {
                    buttonInstructionTextInputLayout.setError("按钮指令不能为空");
                    return;
                }
                
                if (BaseUtils.isNotJson(buttonInstructionTextInputLayoutContent)) {
                    buttonInstructionTextInputLayout.setError("按钮指令格式为JSON");
                    return;
                }
                
                widgetTypeTextInputLayoutContent = "button";
            }
            
            if (widgetTypeLowerCase.contains("switch")) {
                if (switchOnInstructionTextInputLayoutContent.isEmpty()) {
                    switchOnInstructionTextInputLayout.setError("开关指令不能为空");
                    return;
                }
                
                if (BaseUtils.isNotJson(switchOnInstructionTextInputLayoutContent)) {
                    switchOnInstructionTextInputLayout.setError("开指令格式为JSON");
                    return;
                }
                
                if (switchOffInstructionTextInputLayoutContent.isEmpty()) {
                    switchOffInstructionTextInputLayout.setError("开关指令不能为空");
                    return;
                }
                
                if (BaseUtils.isNotJson(switchOffInstructionTextInputLayoutContent)) {
                    switchOffInstructionTextInputLayout.setError("关指令格式为JSON");
                    return;
                }
                widgetTypeTextInputLayoutContent = "switch";
            }
            
            notInstructionLinearLayoutCompat.setVisibility(View.GONE);
            instructionGridAdapter.addItem(new GridViewItemBean(widgetTypeTextInputLayoutContent, widgetNameTextInputLayoutContent, buttonInstructionTextInputLayoutContent, switchOnInstructionTextInputLayoutContent, switchOffInstructionTextInputLayoutContent, false));
            // 使用instructionSharedPreferences
            SharedPreferences.Editor editor = instructionSharedPreferences.edit();
            log("哈哈哈" + instructionGridAdapter.getJson(gson));
            editor.putString(CreateInstructionWidget.KEY_WIDGET_JSON, instructionGridAdapter.getJson(gson));
            editor.apply();
            
            alertDialog.dismiss();
        });
        
    }
    
    /**
     * 长按修改指令
     *
     * @param position               指令下标
     * @param instructionGridAdapter 指令适配器
     */
    private void modifyInstruction(int position, InstructionGridAdapter instructionGridAdapter) {
        View customView = initAddInstructionDialogView();
        
        GridViewItemBean item = (GridViewItemBean) instructionGridAdapter.getItem(position);
        // 禁止编辑
        widgetTypeTextInputLayout.setEnabled(false);
        
        Objects.requireNonNull(widgetTypeTextInputLayout.getEditText()).setText(item.getWidgetType());
        Objects.requireNonNull(widgetNameTextInputLayout.getEditText()).setText(item.getWidgetText());
        Objects.requireNonNull(buttonInstructionTextInputLayout.getEditText()).setText(item.getButtonInstruction());
        Objects.requireNonNull(switchOnInstructionTextInputLayout.getEditText()).setText(item.getSwitchOnInstruction());
        Objects.requireNonNull(switchOffInstructionTextInputLayout.getEditText()).setText(item.getSwitchOffInstruction());
        
        if (widgetTypeTextInputLayout.getEditText().getText().toString().contains("button")) {
            buttonInstructionTextInputLayout.setVisibility(View.VISIBLE);
            switchOnInstructionTextInputLayout.setVisibility(View.GONE);
            switchOffInstructionTextInputLayout.setVisibility(View.GONE);
        }
        
        if (widgetTypeTextInputLayout.getEditText().getText().toString().contains("switch")) {
            buttonInstructionTextInputLayout.setVisibility(View.GONE);
            switchOnInstructionTextInputLayout.setVisibility(View.VISIBLE);
            switchOffInstructionTextInputLayout.setVisibility(View.VISIBLE);
        }
        
        // 修改指令控件弹窗
        MaterialAlertDialogBuilder mdDialog = new MaterialAlertDialogBuilder(this);
        // mdDialog.setIcon(R.drawable.baseline_add_box_24);
        mdDialog.setCancelable(false);
        mdDialog.setTitle("修改指令");
        mdDialog.setView(customView);
        mdDialog.setNeutralButton("删除该指令", (dialog, which) -> {
            // 删除
            instructionGridAdapter.remove(position);
            
            // 使用instructionSharedPreferences
            SharedPreferences.Editor editor = instructionSharedPreferences.edit();
            editor.putString(CreateInstructionWidget.KEY_WIDGET_JSON, instructionGridAdapter.getJson(gson));
            editor.apply();
            
            // 显示无指令布局
            notInstructionLinearLayoutCompat.setVisibility(View.VISIBLE);
            
            toast("删除成功");
        });
        mdDialog.setNegativeButton("取消", null);
        mdDialog.setPositiveButton("修改", null);
        // 用于禁止点击添加后自动关闭弹窗
        AlertDialog alertDialog = mdDialog.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            if (DebounceUtils.isDebounce()) {
                return;
            }
            // 清除控件错误
            widgetTypeTextInputLayout.setError(null);
            widgetNameTextInputLayout.setError(null);
            buttonInstructionTextInputLayout.setError(null);
            switchOnInstructionTextInputLayout.setError(null);
            switchOffInstructionTextInputLayout.setError(null);
            
            String widgetTypeTextInputLayoutContent = Objects.requireNonNull(widgetTypeTextInputLayout.getEditText()).getText().toString();
            String widgetNameTextInputLayoutContent = Objects.requireNonNull(widgetNameTextInputLayout.getEditText()).getText().toString();
            String buttonInstructionTextInputLayoutContent = Objects.requireNonNull(buttonInstructionTextInputLayout.getEditText()).getText().toString();
            String switchOnInstructionTextInputLayoutContent = Objects.requireNonNull(switchOnInstructionTextInputLayout.getEditText()).getText().toString();
            String switchOffInstructionTextInputLayoutContent = Objects.requireNonNull(switchOffInstructionTextInputLayout.getEditText()).getText().toString();
            
            // 一个一个判断, 然后错误的setError
            if (widgetTypeTextInputLayoutContent.isEmpty()) {
                widgetTypeTextInputLayout.setError("控件类型不能为空");
                return;
            }
            
            if (widgetNameTextInputLayoutContent.isEmpty()) {
                widgetNameTextInputLayout.setError("控件名称不能为空");
                return;
            }
            
            if (widgetNameTextInputLayoutContent.length() > 10) {
                widgetNameTextInputLayout.setError("控件名称不能超过10个字符");
                return;
            }
            
            // 下拉框选择button类型
            String widgetTypeLowerCase = widgetTypeTextInputLayoutContent.toLowerCase();
            if (widgetTypeLowerCase.contains("button")) {
                if (buttonInstructionTextInputLayoutContent.isEmpty()) {
                    buttonInstructionTextInputLayout.setError("按钮指令不能为空");
                    return;
                }
                
                if (BaseUtils.isNotJson(buttonInstructionTextInputLayoutContent)) {
                    buttonInstructionTextInputLayout.setError("按钮指令格式为JSON");
                    return;
                }
                widgetTypeTextInputLayoutContent = "button";
            }
            
            // 下拉框选择switch类型
            if (widgetTypeLowerCase.contains("switch")) {
                if (switchOnInstructionTextInputLayoutContent.isEmpty()) {
                    switchOnInstructionTextInputLayout.setError("开关指令不能为空");
                    return;
                }
                
                if (BaseUtils.isNotJson(switchOnInstructionTextInputLayoutContent)) {
                    switchOnInstructionTextInputLayout.setError("开指令格式为JSON");
                    return;
                }
                
                if (switchOffInstructionTextInputLayoutContent.isEmpty()) {
                    switchOffInstructionTextInputLayout.setError("开关指令不能为空");
                    return;
                }
                
                if (BaseUtils.isNotJson(switchOffInstructionTextInputLayoutContent)) {
                    switchOffInstructionTextInputLayout.setError("关指令格式为JSON");
                    return;
                }
                widgetTypeTextInputLayoutContent = "switch";
            }
            
            // log(widgetTypeTextInputLayoutContent, widgetNameTextInputLayoutContent, buttonInstructionTextInputLayoutContent, switchOnInstructionTextInputLayoutContent, switchOffInstructionTextInputLayoutContent);
            
            // 隐藏无指令布局
            notInstructionLinearLayoutCompat.setVisibility(View.GONE);
            
            instructionGridAdapter.changeItem(position, new GridViewItemBean(widgetTypeTextInputLayoutContent, widgetNameTextInputLayoutContent, buttonInstructionTextInputLayoutContent, switchOnInstructionTextInputLayoutContent, switchOffInstructionTextInputLayoutContent, false));
            
            // 使用instructionSharedPreferences
            SharedPreferences.Editor editor = instructionSharedPreferences.edit();
            editor.putString(CreateInstructionWidget.KEY_WIDGET_JSON, instructionGridAdapter.getJson(gson));
            editor.apply();
            
            alertDialog.dismiss();
        });
        
    }
    
    
    /**
     * MQTT配置
     *
     * @param productKey   productKey
     * @param deviceName   deviceName
     * @param deviceSecret deviceSecret
     * @param clientId     clientId
     */
    public void initMqttManager(String productKey, String deviceName, String deviceSecret, String clientId, String subscribeTopic) {
        // AppLog.setLevel(ALog.LEVEL_DEBUG);
        
        LinkKitInitParams params = new LinkKitInitParams();
        
        // yv
        // String productKey = "k1mfojidyyg";
        // String deviceName = "WeChat";
        // String deviceSecret = "c46c4c7fbc9de88c9a281a6b59b14bed";
        // String productSecret = "";
        
        //Step1: 构造设备认证信息
        DeviceInfo deviceInfo = new DeviceInfo();
        // 产品类型
        deviceInfo.productKey = productKey;
        // 设备名称
        deviceInfo.deviceName = deviceName;
        // 设备密钥
        deviceInfo.deviceSecret = deviceSecret;
        // 产品密钥
        // deviceInfo.productSecret = productSecret;
        params.deviceInfo = deviceInfo;
        
        //Step2: 全局默认域名
        params.connectConfig = new IoTApiClientConfig();
        
        //Step3: 物模型缓存
        Map<String, ValueWrapper> propertyValues = new HashMap<>();
        /*
          物模型的数据会缓存到该字段中，不可删除或者设置为空，否则功能会异常
          用户调用物模型上报接口之后，物模型会有相关数据缓存。
         */
        params.propertyValues = propertyValues;
        
        //Step4: mqtt设置
        /*
          Mqtt 相关参数设置，包括接入点等信息，具体参见deviceinfo文件说明
          域名、产品密钥、认证安全模式等；
         */
        IoTMqttClientConfig clientConfig = new IoTMqttClientConfig();
        //cleanSession=1 不接受离线消息
        clientConfig.receiveOfflineMsg = false;
        //mqtt接入点信息
        // clientConfig.channelHost = "iot-06z00ctnl8l2jzz.mqtt.iothub.aliyuncs.com:1883";
        clientConfig.channelHost = clientId;
        params.mqttClientConfig = clientConfig;
        //如果灭屏情况下经常出现设备离线，请参考下述的"设置自定义心跳和解决灭屏情况下的心跳不准问题"一节
        
        //Step5: 高阶功能配置，除物模型外，其余默认均为关闭状态
        IoTDMConfig ioTDMConfig = new IoTDMConfig();
        // 默认开启物模型功能，物模型初始化（包含请求云端物模型）完成后才返回onInitDone
        ioTDMConfig.enableThingModel = true;
        // 默认不开启网关功能，开启之后，初始化的时候会初始化网关模块，获取云端网关子设备列表
        ioTDMConfig.enableGateway = false;
        // 默认不开启，是否开启日志推送功能
        ioTDMConfig.enableLogPush = false;
        params.ioTDMConfig = ioTDMConfig;
        
        //Step6: 下行消息处理回调设置
        LinkKit.getInstance().registerOnPushListener(new IConnectNotifyListener() {
            @Override
            public void onNotify(String s, String s1, AMessage aMessage) {
                // 这里可以拿到服务器推送过来的信息
                String retuenString = new String((byte[]) aMessage.getData());
                
                addLog(String.format("状态: %1$s\ntopic: %2$s\npayload: %3$s", s, s1, retuenString), LogItemBean.cloudRole());
                
                activityRealTimeRecycleViewAdapter.modifyCard(retuenString);
            }
            
            @Override
            public boolean shouldHandle(String s, String s1) {
                Log.i(TAG, "shouldHandle() called with: topic = [" + s + "], payload = [" + s1 + "]");
                // toast("shouldHandle() called with: topic = [" + s + "], payload = [" + s1 + "]");
                
                addLog(String.format("topic: %1$s\npayload: %2$s", s, s1), LogItemBean.cloudRole());
                
                return true;
            }
            
            @Override
            public void onConnectStateChange(String connectId, ConnectState connectState) {
                // 对应连接类型的连接状态变化回调，具体连接状态参考SDK ConnectState
                Log.i(TAG, "onConnectStateChange() called with: connectId = [" + connectId + "], connectState = [" + connectState + "]");
                
                // addLog("onConnectStateChange() called with: connectId = [" + connectId + "], connectState = [" + connectState + "]", LogItemBean.cloudRole());
                
                //首次连云可能失败。对于首次连云失败，SDK会报出ConnectState.CONNECTFAIL这种状态。对于这种场景，用户可以尝试若干次后退出，也可以一直重试直到连云成功
                // 以下是首次建连时用户主动重试的一个参考实现，用户可以打开下面注释使能下述代码
                if (connectState == ConnectState.CONNECTFAIL) {
                    try {
                        Thread.sleep(2000);
                        PersistentNet.getInstance().reconnect();
                        // PersistentNet.getInstance().destroy();
                    } catch (Exception e) {
                        Log.i(TAG, "exception is " + e);
                    }
                    
                    Log.i(TAG, "onConnectStateChange（） 在连接失败时尝试重新连接");
                    
                    addLog("onConnectStateChange() 在连接失败时尝试重新连接", LogItemBean.appRole());
                }
                
                //SDK连云成功后，后续如果网络波动导致连接断开时，SDK会抛出ConnectState.DISCONNECTED这种状态。在这种情况下，SDK会自动尝试重连，重试的间隔是1s、2s、4s、8s...128s...128s，到了最大间隔128s后，会一直以128s为间隔重连直到连云成功。
            }
        });
        
        //对于一型一密免预注册的设备, 设备连云时要用上deviceToken和clientId
        //Step7: 一型一密免预注册设置，默认关闭
        // MqttConfigure.deviceToken = DemoApplication.deviceToken;
        // MqttConfigure.clientId = DemoApplication.clientId;
        
        //Step8: H2文件上传设置
        /*
          如果要用到HTTP2文件上传, 需要用户设置域名.默认关闭
         */
        // IoTH2Config ioTH2Config = new IoTH2Config();
        // ioTH2Config.clientId = "client-id";
        // ioTH2Config.endPoint = "https://" + productKey + ioTH2Config.endPoint;// 线上环境
        // params.iotH2InitParams = ioTH2Config;
        
        /*
          设备初始化建联
          onError 初始化建联失败，如果因网络问题导致初始化失败，需要用户重试初始化
          onInitDone 初始化成功
         */
        LinkKit.getInstance().init(this, params, new ILinkKitConnectListener() {
            @Override
            public void onError(AError error) {
                Log.i(TAG, "onError() 调用时间：error = [" + (error.getMsg()) + "]");
                
                String errorInfo = "Sub Domain: " + error.getSubDomain() + "\n" +
                                           "Sub Code: " + error.getSubCode() + "\n" +
                                           "Sub Message: " + error.getSubMsg() + "\n" +
                                           "Message: " + error.getMsg() + "\n" +
                                           "Code: " + error.getCode() + "\n" +
                                           "Origin Response Object: " + error.getOriginResponseObject() + "\n" +
                                           "Domain: " + error.getDomain();
                addLog("onError()初始化错误: \n" + errorInfo, LogItemBean.cloudRole());
            }
            
            @Override
            public void onInitDone(Object data) {
                addLog("onInitDone() data: " + ((InitResult) data).tsl, LogItemBean.cloudRole());
                
                // 开始用户自己的业务
                subscribe(subscribeTopic);
            }
        });
    }
    
    
    /**
     * 订阅
     */
    private void subscribe(String subscribeTopic) {
        // 订阅
        MqttSubscribeRequest subscribeRequest = new MqttSubscribeRequest();
        // subTopic 替换成您需要订阅的 topic
        // subscribeRequest.topic = "/k1mfojidyyg/WeChat/user/get";
        subscribeRequest.topic = subscribeTopic;
        subscribeRequest.isSubscribe = true;
        // 支持0或者1
        subscribeRequest.qos = 0;
        LinkKit.getInstance().subscribe(subscribeRequest, new IConnectSubscribeListener() {
            @Override
            public void onSuccess() {
                // 订阅成功
                Log.i(TAG, "订阅成功");
                toast("订阅成功");
                addLog("订阅成功", LogItemBean.systemRole());
            }
            
            @Override
            public void onFailure(AError error) {
                // 订阅失败
                toast("订阅失败", error.toString());
                
                String errorInfo = "Sub Domain: " + error.getSubDomain() + "\n" +
                                           "Sub Code: " + error.getSubCode() + "\n" +
                                           "Sub Message: " + error.getSubMsg() + "\n" +
                                           "Message: " + error.getMsg() + "\n" +
                                           "Code: " + error.getCode() + "\n" +
                                           "Origin Response Object: " + error.getOriginResponseObject() + "\n" +
                                           "Domain: " + error.getDomain();
                addLog("onFailure() 订阅失败: \n" + errorInfo, LogItemBean.systemRole());
            }
        });
    }
    
    
    /**
     * 发布消息
     *
     * @param content 发布的消息内容
     */
    public void sendMessage(String content) {
        addLog(content, LogItemBean.appRole());
        
        if (sanYuanSharedPreferences == null) {
            addLog("没连接", LogItemBean.systemRole());
            return;
        }
        
        String spPublishTopic = sanYuanSharedPreferences.getString(SanYuanZhu.KEY_PUBLISH_TOPIC, null);
        
        if (TextUtils.isEmpty(spPublishTopic)) {
            addLog("没配置", LogItemBean.systemRole());
            return;
        }
        
        // 发布
        MqttPublishRequest request = new MqttPublishRequest();
        // 设置是否需要应答。设置为true，表示期望收到物联网平台的下行回复。
        request.isRPC = true;
        // 设置qos
        request.qos = 0;
        // 设置topic，设备通过该Topic向物联网平台发送消息。以下Topic为示例，需替换为用户自己设备的Topic。
        // request.topic = "/k1mfojidyyg/WeChat/user/WeChat";
        request.topic = spPublishTopic;
        //设置物联网平台答复的topic，若不设置，则默认为 topic+“_reply”。
        // request.replyTopic = "/a18wP******/LightSwitch/user/update_reply";
        // 设置需要发
        request.payloadObj = content;
        //demo的BaseTemplateActivity提供了参考的消息响应的类, 下行消息会在其中的onResponse中给到用户
        LinkKit.getInstance().publish(request, new IConnectSendListener() {
            @Override
            public void onResponse(ARequest aRequest, AResponse aResponse) {
                // 发布成功
                Log.i(TAG, "发布成功");
                // 获取下行消息
                // DemoBaseTemplateActivity.onResponse(aRequest, aResponse);
                // 获取下行消息的topic
                // String topic = aResponse.getTopic();
                // 获取下行消息的payload
                // String payload = aResponse.payloadObj;
                // 获取下行消息的qos
                // int qos = aResponse.qos;
                // 获取下行消息的msgId
                // String msgId = aResponse.msgId;
                Log.i(TAG, "aRequest: " + aRequest.toString());
                Log.i(TAG, "aResponse: " + aResponse.data.toString());
                
                addLog("发布成功\naResponse: " + aResponse.data.toString() + "\naRequest: " + aRequest, LogItemBean.cloudRole());
            }
            
            @Override
            public void onFailure(ARequest aRequest, AError error) {
                // 发布失败
                String errorInfo = "Sub Domain: " + error.getSubDomain() + "\n" +
                                           "Sub Code: " + error.getSubCode() + "\n" +
                                           "Sub Message: " + error.getSubMsg() + "\n" +
                                           "Message: " + error.getMsg() + "\n" +
                                           "Code: " + error.getCode() + "\n" +
                                           "Origin Response Object: " + error.getOriginResponseObject() + "\n" +
                                           "Domain: " + error.getDomain();
                
                addLog("onFailure() 发布失败: " + errorInfo, LogItemBean.cloudRole());
                toast("发布失败");
                
            }
        });
    }
    
    
    /**
     * 取消订阅
     */
    private void unSubscribe() {
        // 取消订阅
        MqttSubscribeRequest unSubRequest = new MqttSubscribeRequest();
        // unSubTopic 替换成您需要取消订阅的topic
        unSubRequest.topic = "unSubTopic";
        unSubRequest.isSubscribe = false;
        LinkKit.getInstance().unsubscribe(unSubRequest, new IConnectUnscribeListener() {
            @Override
            public void onSuccess() {
                // 取消订阅成功
                Log.i(TAG, "取消订阅成功");
                toast("取消订阅成功");
            }
            
            @Override
            public void onFailure(AError aError) {
                // 取消订阅失败
                Log.i(TAG, "取消订阅失败");
                toast("取消订阅失败", aError.toString());
            }
        });
    }
    
    private void saveInstructionData() {
        if (instructionGridAdapter != null && instructionSharedPreferences != null) {
            SharedPreferences.Editor editor = instructionSharedPreferences.edit();
            editor.putString(CreateInstructionWidget.KEY_WIDGET_JSON, instructionGridAdapter.getJson(gson));
            editor.apply();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveInstructionData();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        saveInstructionData();
    }
}