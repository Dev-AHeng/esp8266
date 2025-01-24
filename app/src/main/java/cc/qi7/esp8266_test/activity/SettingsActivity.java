package cc.qi7.esp8266_test.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.adapter.MainActivityRealTimeRecycleViewAdapter;
import cc.qi7.esp8266_test.config.Settings;

/**
 * 设置界面
 *
 * @author Dev_Heng
 * @date 2024年9月21日17:24:13
 */
public class SettingsActivity extends BaseActivity {
    // 使用常量替代魔法数字
    private static final String FORMAT_SLIDER_LABEL = " %d%s ";
    private static final String FORMAT_TEXT_SIZE = "%s(%.0fpx)";
    
    // UI组件分组管理
    private final UIComponents uiComponents = new UIComponents();
    private MainActivityRealTimeRecycleViewAdapter realTimeRecycleViewAdapter;
    private SharedPreferences sharedPreferences;
    
    // 当前设置值
    private SettingsValues currentValues = new SettingsValues();
    
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            
            sharedPreferences = getSharedPreferences(Settings.SAVE_TABLE_NAME, Context.MODE_PRIVATE);
            
            initViews();
            setupSliders();
            setupListeners();
            initTextSize();
        } catch (Exception e) {
            e.printStackTrace();
            toast("初始化失败: " + e.getMessage());
            finish();
        }
    }
    
    // 使用内部类管理UI组件
    private static class UIComponents {
        MaterialSwitch borderSwitch;
        Slider prefixTextSizeSlider, placeholderTextSizeSlider, suffixTextSizeSlider,
                dateTextSizeSlider, gridAmountSlider;
        MaterialButton resetButton;
        TextView prefixTextSizeDesc, placeholderTextSizeDesc, suffixTextSizeDesc,
                dateTextSizeDesc, gridAmount;
        RecyclerView realTimeDataRecyclerView;
    }
    
    // 使用数据类管理设置值
    private static class SettingsValues {
        float gridColumn;
        float prefixTextSize;
        float placeholderTextSize;
        float suffixTextSize;
        float timeTextSize;
    }
    
    private void initViews() {
        setupToolbar();
        setupRecyclerView();
        initUIComponents();
    }
    
    private void setupToolbar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());
    }
    
    private void initUIComponents() {
        uiComponents.borderSwitch = findViewById(R.id.borderSwitch);
        uiComponents.prefixTextSizeSlider = findViewById(R.id.prefixTextSize);
        uiComponents.placeholderTextSizeSlider = findViewById(R.id.placeholderTextSize);
        uiComponents.suffixTextSizeSlider = findViewById(R.id.suffixTextSize);
        uiComponents.dateTextSizeSlider = findViewById(R.id.dateTextSize);
        uiComponents.gridAmountSlider = findViewById(R.id.gridAmountSlider);
        
        uiComponents.resetButton = findViewById(R.id.resetButton);
        
        uiComponents.prefixTextSizeDesc = findViewById(R.id.prefixTextSizeDesc);
        uiComponents.placeholderTextSizeDesc = findViewById(R.id.placeholderTextSizeDesc);
        uiComponents.suffixTextSizeDesc = findViewById(R.id.suffixTextSizeDesc);
        uiComponents.dateTextSizeDesc = findViewById(R.id.dateTextSizeDesc);
        uiComponents.gridAmount = findViewById(R.id.gridAmount);
        
        if (realTimeRecycleViewAdapter != null) {
            setDemoMode(true);
        }
    }
    
    private void setupRecyclerView() {
        try {
            LinearLayoutCompat notRealTimeCardLayout = findViewById(R.id.notRealTimeCardLinearLayoutCompat);
            if (notRealTimeCardLayout == null) {
                toast("布局初始化失败");
                return;
            }
            
            uiComponents.realTimeDataRecyclerView = findViewById(R.id.showRealTimeDataRecyclerView);
            if (uiComponents.realTimeDataRecyclerView == null) {
                toast("RecyclerView初始化失败");
                return;
            }
            
            uiComponents.realTimeDataRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            uiComponents.realTimeDataRecyclerView.setItemAnimator(null);
            
            realTimeRecycleViewAdapter = new MainActivityRealTimeRecycleViewAdapter(this, notRealTimeCardLayout);
            uiComponents.realTimeDataRecyclerView.setAdapter(realTimeRecycleViewAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            toast("RecyclerView设置失败: " + e.getMessage());
        }
    }
    
    private void setupSliders() {
        uiComponents.gridAmountSlider.setLabelFormatter(value -> 
            String.format(FORMAT_SLIDER_LABEL, (int)value, "列"));
        
        Slider[] sliders = {uiComponents.prefixTextSizeSlider, 
                           uiComponents.placeholderTextSizeSlider,
                           uiComponents.suffixTextSizeSlider, 
                           uiComponents.dateTextSizeSlider};
                           
        for (Slider slider : sliders) {
            slider.setLabelFormatter(this::formatSliderLabel);
        }
    }
    
    private void setupListeners() {
        setupBorderSwitchListener();
        setupResetButtonListener();
        setupSlidersListeners();
    }
    
    private void setupBorderSwitchListener() {
        uiComponents.borderSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                                                                     realTimeRecycleViewAdapter.isBorder(isChecked));
    }
    
    private void setupResetButtonListener() {
        uiComponents.resetButton.setOnClickListener(v -> {
            isResetting = true;
            resetToDefaultValues();
            isResetting = false;
        });
    }
    
    private void resetToDefaultValues() {
        currentValues.gridColumn = Settings.DEFAULT_GRID_COLUMN;
        currentValues.prefixTextSize = Settings.DEFAULT_PREFIX_TEXT_SIZE;
        currentValues.placeholderTextSize = Settings.DEFAULT_PLACEHOLDER_TEXT_SIZE;
        currentValues.suffixTextSize = Settings.DEFAULT_SUFFIX_TEXT_SIZE;
        currentValues.timeTextSize = Settings.DEFAULT_TIME_TEXT_SIZE;
        
        setCardTextSize(currentValues.gridColumn,
                currentValues.prefixTextSize,
                currentValues.placeholderTextSize,
                currentValues.suffixTextSize,
                currentValues.timeTextSize);
                
        SharedPreferences.Editor editor = sharedPreferences.edit();
        saveValuesToPreferences(editor);
        editor.apply();
    }
    
    @Override
    public void onBackPressed() {
        saveSettings();
        setResultAndFinish();
        super.onBackPressed();
    }
    
    private void saveSettings() {
        if (!isResetting) {
            updateCurrentValues();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        saveValuesToPreferences(editor);
        editor.apply();
    }
    
    private void updateCurrentValues() {
        currentValues.gridColumn = uiComponents.gridAmountSlider.getValue();
        currentValues.prefixTextSize = realTimeRecycleViewAdapter.getPrefixTextSize();
        currentValues.placeholderTextSize = realTimeRecycleViewAdapter.getPlaceholderTextSize();
        currentValues.suffixTextSize = realTimeRecycleViewAdapter.getSuffixTextSize();
        currentValues.timeTextSize = realTimeRecycleViewAdapter.getTimeTextSize();
    }
    
    private void saveValuesToPreferences(SharedPreferences.Editor editor) {
        editor.putFloat(Settings.KEY_GRID_COLUMN, currentValues.gridColumn);
        editor.putFloat(Settings.KEY_PREFIX_TEXT_SIZE, currentValues.prefixTextSize);
        editor.putFloat(Settings.KEY_PLACEHOLDER_TEXT_SIZE, currentValues.placeholderTextSize);
        editor.putFloat(Settings.KEY_SUFFIX_TEXT_SIZE, currentValues.suffixTextSize);
        editor.putFloat(Settings.KEY_TIME_TEXT_SIZE, currentValues.timeTextSize);
    }
    
    private void setResultAndFinish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Settings.KEY_GRID_COLUMN, currentValues.gridColumn);
        returnIntent.putExtra(Settings.KEY_PREFIX_TEXT_SIZE, currentValues.prefixTextSize);
        returnIntent.putExtra(Settings.KEY_PLACEHOLDER_TEXT_SIZE, currentValues.placeholderTextSize);
        returnIntent.putExtra(Settings.KEY_SUFFIX_TEXT_SIZE, currentValues.suffixTextSize);
        returnIntent.putExtra(Settings.KEY_TIME_TEXT_SIZE, currentValues.timeTextSize);
        setResult(Activity.RESULT_OK, returnIntent);
        
        toast("设置成功");
    }
    
    private void setupSlidersListeners() {
        setupSliderListener(uiComponents.gridAmountSlider, uiComponents.gridAmount,
                "卡片列数", value -> uiComponents.realTimeDataRecyclerView.setLayoutManager(
                        new GridLayoutManager(this, (int) value)));
        
        setupSliderListener(uiComponents.prefixTextSizeSlider, uiComponents.prefixTextSizeDesc,
                "前缀文本大小", realTimeRecycleViewAdapter::setPrefixTextSize);
        
        setupSliderListener(uiComponents.placeholderTextSizeSlider, uiComponents.placeholderTextSizeDesc,
                "数据文本大小", realTimeRecycleViewAdapter::setPlaceholderTextSize);
        
        setupSliderListener(uiComponents.suffixTextSizeSlider, uiComponents.suffixTextSizeDesc,
                "后缀文本大小", realTimeRecycleViewAdapter::setSuffixTextSize);
        
        setupSliderListener(uiComponents.dateTextSizeSlider, uiComponents.dateTextSizeDesc,
                "日期文本大小", realTimeRecycleViewAdapter::setTimeTextSize);
    }
    
    @SuppressLint({"DefaultLocale", "SetTextI18x"})
    private void setupSliderListener(Slider slider, TextView textView, String label, SliderValueConsumer consumer) {
        slider.addOnChangeListener((s, value, fromUser) -> {
            consumer.accept(value);
            textView.setText(String.format(FORMAT_TEXT_SIZE, label, value));
        });
    }
    
    private String formatSliderLabel(float value) {
        return String.format(FORMAT_SLIDER_LABEL, (int)value, "px");
    }
    
    private void loadDefaultTextSizes() {
        currentValues.prefixTextSize = sharedPreferences.getFloat(
            Settings.KEY_PREFIX_TEXT_SIZE, Settings.DEFAULT_PREFIX_TEXT_SIZE);
        currentValues.placeholderTextSize = sharedPreferences.getFloat(
            Settings.KEY_PLACEHOLDER_TEXT_SIZE, Settings.DEFAULT_PLACEHOLDER_TEXT_SIZE);
        currentValues.suffixTextSize = sharedPreferences.getFloat(
            Settings.KEY_SUFFIX_TEXT_SIZE, Settings.DEFAULT_SUFFIX_TEXT_SIZE);
        currentValues.timeTextSize = sharedPreferences.getFloat(
            Settings.KEY_TIME_TEXT_SIZE, Settings.DEFAULT_TIME_TEXT_SIZE);
    }
    
    private void setCardTextSize(float gridColumn, float prefixTextSize,
                                 float placeholderTextSize, float suffixTextSize,
                                 float timeTextSize) {
        uiComponents.realTimeDataRecyclerView.setLayoutManager(
            new GridLayoutManager(this, (int) gridColumn));
        uiComponents.gridAmountSlider.setValue(gridColumn);
        uiComponents.prefixTextSizeSlider.setValue(prefixTextSize);
        uiComponents.placeholderTextSizeSlider.setValue(placeholderTextSize);
        uiComponents.suffixTextSizeSlider.setValue(suffixTextSize);
        uiComponents.dateTextSizeSlider.setValue(timeTextSize);
    }
    
    private void initTextSize() {
        uiComponents.realTimeDataRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        uiComponents.realTimeDataRecyclerView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        
                        if (hasStoredSettings()) {
                            loadStoredSettings();
                        } else {
                            loadDefaultSettings();
                        }
                        
                        setCardTextSize(currentValues.gridColumn,
                                currentValues.prefixTextSize,
                                currentValues.placeholderTextSize,
                                currentValues.suffixTextSize,
                                currentValues.timeTextSize);
                        
                        saveDefaultValuesIfNeeded();
                    }
                });
    }
    
    private boolean hasStoredSettings() {
        return sharedPreferences.contains(Settings.KEY_GRID_COLUMN)
                       && sharedPreferences.contains(Settings.KEY_PREFIX_TEXT_SIZE)
                       && sharedPreferences.contains(Settings.KEY_PLACEHOLDER_TEXT_SIZE)
                       && sharedPreferences.contains(Settings.KEY_SUFFIX_TEXT_SIZE)
                       && sharedPreferences.contains(Settings.KEY_TIME_TEXT_SIZE);
    }
    
    private void loadStoredSettings() {
        currentValues.gridColumn = sharedPreferences.getFloat(
                Settings.KEY_GRID_COLUMN, Settings.DEFAULT_GRID_COLUMN);
        loadDefaultTextSizes();
    }
    
    private void loadDefaultSettings() {
        currentValues.gridColumn = Settings.DEFAULT_GRID_COLUMN;
        currentValues.prefixTextSize = realTimeRecycleViewAdapter.getPrefixTextSize();
        currentValues.placeholderTextSize = realTimeRecycleViewAdapter.getPlaceholderTextSize();
        currentValues.suffixTextSize = realTimeRecycleViewAdapter.getSuffixTextSize();
        currentValues.timeTextSize = realTimeRecycleViewAdapter.getTimeTextSize();
    }
    
    private void saveDefaultValuesIfNeeded() {
        if (!hasDefaultValuesStored()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(Settings.KEY_DEFAULT_GRID_COLUMN, Settings.DEFAULT_GRID_COLUMN);
            editor.putFloat(Settings.KEY_DEFAULT_PREFIX_TEXT_SIZE, realTimeRecycleViewAdapter.getPrefixTextSize());
            editor.putFloat(Settings.KEY_DEFAULT_PLACEHOLDER_TEXT_SIZE, realTimeRecycleViewAdapter.getPlaceholderTextSize());
            editor.putFloat(Settings.KEY_DEFAULT_SUFFIX_TEXT_SIZE, realTimeRecycleViewAdapter.getSuffixTextSize());
            editor.putFloat(Settings.KEY_DEFAULT_TIME_TEXT_SIZE, realTimeRecycleViewAdapter.getTimeTextSize());
            editor.apply();
        }
    }
    
    private boolean hasDefaultValuesStored() {
        return sharedPreferences.contains(Settings.KEY_DEFAULT_GRID_COLUMN)
                       && sharedPreferences.contains(Settings.KEY_DEFAULT_PREFIX_TEXT_SIZE)
                       && sharedPreferences.contains(Settings.KEY_DEFAULT_PLACEHOLDER_TEXT_SIZE)
                       && sharedPreferences.contains(Settings.KEY_DEFAULT_SUFFIX_TEXT_SIZE)
                       && sharedPreferences.contains(Settings.KEY_DEFAULT_TIME_TEXT_SIZE);
    }
    
    private void setDemoMode(boolean demoMode) {
        View addRealTimeDataView = findViewById(R.id.addRealTimeData);
        if (addRealTimeDataView != null) {
            addRealTimeDataView.setVisibility(demoMode ? View.GONE : View.VISIBLE);
        }
        
        if (realTimeRecycleViewAdapter != null) {
            realTimeRecycleViewAdapter.setDemoMode(demoMode);
        }
    }
    
    @FunctionalInterface
    interface SliderValueConsumer {
        void accept(float value);
    }
    
    private boolean isResetting = false;
}
