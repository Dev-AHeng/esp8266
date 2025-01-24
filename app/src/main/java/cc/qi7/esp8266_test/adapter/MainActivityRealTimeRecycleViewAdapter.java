package cc.qi7.esp8266_test.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.bean.CardGroupBean;
import cc.qi7.esp8266_test.bean.EditPlaceholderFragmentItemBean;
import cc.qi7.esp8266_test.config.PublicConfig;
import cc.qi7.esp8266_test.utils.Clone;
import cc.qi7.esp8266_test.utils.DateTimeUtil;
import cc.qi7.esp8266_test.utils.DebounceUtils;

/**
 * MainActivity实时卡片的RecycleView适配器
 *
 * @author AHeng
 * @date 2024/08/20 1:20
 */
public class MainActivityRealTimeRecycleViewAdapter extends ListAdapter<EditPlaceholderFragmentItemBean, MainActivityRealTimeRecycleViewAdapter.MainActivityRealTimeViewHolder> {
    private static final String TAG = "日志";
    private final Context context;
    private final LinearLayoutCompat notRealTimeCardLinearLayoutCompat;
    private final List<CardGroupBean> cardGroupBeans = new CopyOnWriteArrayList<>();
    // 是否演示模式
    private boolean isDemoMode = false;
    
    private float prefixTextSize;
    private float placeholderTextSize;
    private float suffixTextSize;
    private float timeTextSize;
    
    private boolean isBorder;
    
    public MainActivityRealTimeRecycleViewAdapter(Context context, LinearLayoutCompat notRealTimeCardLinearLayoutCompat) {
        super(DIFF_CALLBACK);
        this.notRealTimeCardLinearLayoutCompat = notRealTimeCardLinearLayoutCompat;
        this.context = context;
        
        // // 将sp单位的值转换为px单位的值
        // this.prefixTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16.0f, context.getResources().getDisplayMetrics());
        // this.placeholderTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 32.0f, context.getResources().getDisplayMetrics());
        // this.suffixTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16.0f, context.getResources().getDisplayMetrics());
        // this.timeTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10.0f, context.getResources().getDisplayMetrics());
        
        this.isBorder = false;
        
        start();
        prompt();
    }
    
    @NonNull
    @Override
    public MainActivityRealTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_activity_real_time_recycle_view, parent, false);
        return new MainActivityRealTimeViewHolder(inflate, isDemoMode);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MainActivityRealTimeViewHolder holder, int position) {
        holder.bind(getItem(position), isBorder, prefixTextSize, placeholderTextSize, suffixTextSize, timeTextSize);
        
        prefixTextSize = holder.getPrefixTextSize();
        placeholderTextSize = holder.getPlaceholderTextSize();
        suffixTextSize = holder.getSuffixTextSize();
        timeTextSize = holder.getTimeTextSize();
        
        // 打印当前的文本大小
        // Log.d(TAG, "prefixTextSize: " + prefixTextSize);
        // Log.d(TAG, "placeholderTextSize: " + placeholderTextSize);
        // Log.d(TAG, "suffixTextSize: " + suffixTextSize);
        // Log.d(TAG, "timeTextSize: " + timeTextSize);
        
    }
    
    
    /**
     * 设置卡片字体大小
     *
     * @param prefixTextSize      前缀文字
     * @param placeholderTextSize 占位符文字
     * @param suffixTextSize      后缀
     * @param timeTextSize        时间
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setCardItemTextSizeBean(float prefixTextSize, float placeholderTextSize, float suffixTextSize, float timeTextSize) {
        this.prefixTextSize = prefixTextSize;
        this.placeholderTextSize = placeholderTextSize;
        this.suffixTextSize = suffixTextSize;
        this.timeTextSize = timeTextSize;
        notifyDataSetChanged();
    }
    
    /**
     * 设置前缀文字大小
     *
     * @param prefixTextSize 前缀文字大小
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setPrefixTextSize(float prefixTextSize) {
        this.prefixTextSize = prefixTextSize;
        notifyDataSetChanged();
    }
    
    /**
     * 设置占位符文字大小
     *
     * @param placeholderTextSize 占位符文字大小
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setPlaceholderTextSize(float placeholderTextSize) {
        this.placeholderTextSize = placeholderTextSize;
        notifyDataSetChanged();
    }
    
    /**
     * 设置后缀文字大小
     *
     * @param suffixTextSize 后缀文字大小
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setSuffixTextSize(float suffixTextSize) {
        this.suffixTextSize = suffixTextSize;
        notifyDataSetChanged();
    }
    
    /**
     * 设置时间文字大小
     *
     * @param timeTextSize 时间文字大小
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setTimeTextSize(float timeTextSize) {
        this.timeTextSize = timeTextSize;
        notifyDataSetChanged();
    }
    
    /**
     * 设置边框
     *
     * @param isBorder 是否显示边框
     */
    @SuppressLint("NotifyDataSetChanged")
    public void isBorder(boolean isBorder) {
        this.isBorder = isBorder;
        notifyDataSetChanged();
    }
    
    /**
     * 获取前缀文本的字体大小
     *
     * @return 前缀文本的字体大小
     */
    public synchronized float getPrefixTextSize() {
        return prefixTextSize;
    }
    
    /**
     * 获取占位符文本的字体大小
     *
     * @return 占位符文本的字体大小
     */
    public synchronized float getPlaceholderTextSize() {
        return placeholderTextSize;
    }
    
    /**
     * 获取后缀文本的字体大小
     *
     * @return 后缀文本的字体大小
     */
    public synchronized float getSuffixTextSize() {
        return suffixTextSize;
    }
    
    /**
     * 获取时间文本的字体大小
     *
     * @return 时间文本的字体大小
     */
    public synchronized float getTimeTextSize() {
        return timeTextSize;
    }
    
    
    /**
     * 深拷贝提交数据
     *
     * @param editPlaceholderFragmentItemBeanList 数据
     */
    public void submitDeepCopyList(List<EditPlaceholderFragmentItemBean> editPlaceholderFragmentItemBeanList) {
        try {
            editPlaceholderFragmentItemBeanList = Clone.deepCopyList(editPlaceholderFragmentItemBeanList);
        } catch (IOException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        submitList(editPlaceholderFragmentItemBeanList);
        
        prompt();
    }
    
    /**
     * 表没有数据就显示提示布局
     */
    private void prompt() {
        if (cardGroupBeans.isEmpty()) {
            notRealTimeCardLinearLayoutCompat.setVisibility(View.VISIBLE);
        } else {
            notRealTimeCardLinearLayoutCompat.setVisibility(View.GONE);
        }
    }
    
    /**
     * 进入加载列表
     */
    public void start() {
        // 反序列化加载本地数据
        File serFile = new File(context.getFilesDir(), PublicConfig.CARD_GROUP_BEAN);
        if (serFile.exists()) {
            // 反序列化
            try (FileInputStream fis = context.openFileInput(PublicConfig.CARD_GROUP_BEAN);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                // 本地数据
                cardGroupBeans.addAll((List<CardGroupBean>) ois.readObject());
              
                // 显示
                submitList(cardGroupBeansToEditPlaceholderFragmentItemBeans(cardGroupBeans));
            } catch (Exception e) {
                toast("反序列化失败");
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * 进入软件添加卡片, 新增卡片
     *
     * @param cardGroupBean 待添加的卡片
     */
    public void addCard(CardGroupBean cardGroupBean) {
        // 向数组添加数据
        cardGroupBeans.add(cardGroupBean);
        
        // 向本地写入数据
        saveToLocal();
        
        // 显示
        submitDeepCopyList(cardGroupBeansToEditPlaceholderFragmentItemBeans(cardGroupBeans));
    }
    
    /**
     * 长按删除卡片
     *
     * @param itemBean 待删除的卡片
     */
    public void removeCard(EditPlaceholderFragmentItemBean itemBean) {
        // 向数组删除数据, 删除同一组的卡片
        for (int index = 0; index < cardGroupBeans.size(); index++) {
            if (cardGroupBeans.get(index).getCardItemBeanMap().containsValue(itemBean)) {
                cardGroupBeans.remove(index);
                break;
            }
        }
        
        // 向本地写入数据
        saveToLocal();
        
        // 显示
        submitDeepCopyList(cardGroupBeansToEditPlaceholderFragmentItemBeans(cardGroupBeans));
    }
    
    /**
     * 修改更新实时数据
     *
     * @param data 数据
     */
    public void modifyCard(String data) {
        // 向数组修改数据
        if (cardGroupBeans.isEmpty()) {
            return;
        }
        
        // 去除空格&换行
        data = data.replaceAll("\\s+", "");
        
        for (CardGroupBean cardGroupBean : cardGroupBeans) {
            AtomicInteger counter = new AtomicInteger(1);
            for (String splitItem : cardGroupBean.getPlaceholderSplit()) {
                if (!data.contains(splitItem)) {
                    break;
                }
                
                if (counter.getAndIncrement() == cardGroupBean.getPlaceholderSplit().length) {
                    Map<String, String> placeholderValueMap = getPlaceholderValueMap(data, cardGroupBean.getPlaceholderModelContent());
                    // 遍历placeholderValueMap
                    for (Map.Entry<String, String> entry : placeholderValueMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        
                        Objects.requireNonNull(cardGroupBean.getCardItemBeanMap().get(key)).setPlaceholder(value);
                        Objects.requireNonNull(cardGroupBean.getCardItemBeanMap().get(key)).setTime(DateTimeUtil.getCurrentFormattedTime());
                    }
                    
                    List<EditPlaceholderFragmentItemBean> editPlaceholderFragmentItemBeans = new ArrayList<>();
                    for (CardGroupBean localCardGroupBean : cardGroupBeans) {
                        Map<String, EditPlaceholderFragmentItemBean> cardItemBeanMap = localCardGroupBean.getCardItemBeanMap();
                        editPlaceholderFragmentItemBeans.addAll(cardItemBeanMap.values());
                    }
                    
                    // 显示
                    submitDeepCopyList(editPlaceholderFragmentItemBeans);
                }
            }
        }
    }
    
    /**
     * 获取占位符对应的值 按@x@中的x值获取(推荐)
     *
     * @param sourceJson Json源
     * @param modelJson  Json模板
     * @return map
     */
    public static Map<String, String> getPlaceholderValueMap(String sourceJson, String modelJson) {
        sourceJson = sourceJson.replaceAll("\\s+", "");
        modelJson = modelJson.replaceAll("\\s+", "");
        
        String regex = "@\\d+@";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(modelJson);
        
        Map<String, String> modelStringMap = new HashMap<>();
        
        Map<String, Integer> modelPositionMap = new HashMap<>();
        
        int count = 0;
        while (matcher.find()) {
            // 记录占位符@x@的位置  @1@在模版中的第4位, @2@在模版中的第1位
            modelPositionMap.put(matcher.group(), count++);
        }
        
        String[] jsonSplit = modelJson.split(regex);
        
        try {
            for (int index = 0; index < count; index++) {
                String format = String.format("@%s@", index + 1);
                
                int position = modelPositionMap.get(format);
                
                int i = sourceJson.indexOf(jsonSplit[position]) + jsonSplit[position].length();
                int i1 = sourceJson.indexOf(jsonSplit[position + 1]);
                String substring = sourceJson.substring(i, i1);
                modelStringMap.put(format, substring);
            }
        } catch (Exception exception) {
            System.out.println(exception.toString());
        }
        return modelStringMap;
    }
    
    /**
     * 向本地写入数据
     */
    public void saveToLocal() {
        try (FileOutputStream fos = context.openFileOutput(PublicConfig.CARD_GROUP_BEAN, MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(cardGroupBeans);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 将卡片组数据转换为卡片数据
     *
     * @param cardGroupBeans 卡片组数据
     * @return 卡片数据
     */
    private List<EditPlaceholderFragmentItemBean> cardGroupBeansToEditPlaceholderFragmentItemBeans(List<CardGroupBean> cardGroupBeans) {
        List<EditPlaceholderFragmentItemBean> editPlaceholderFragmentItemBeans = new ArrayList<>();
        for (CardGroupBean cardGroupBean : cardGroupBeans) {
            Map<String, EditPlaceholderFragmentItemBean> cardItemBeanMap = cardGroupBean.getCardItemBeanMap();
            editPlaceholderFragmentItemBeans.addAll(cardItemBeanMap.values());
        }
        return editPlaceholderFragmentItemBeans;
    }
    
    public void setDemoMode(boolean demoMode) {
        this.isDemoMode = demoMode;
    }
    
    /**
     * ViewHolder
     */
    public class MainActivityRealTimeViewHolder extends RecyclerView.ViewHolder {
        private EditPlaceholderFragmentItemBean itemBean;
        private final AppCompatTextView prefixAppCompatTextView;
        private final AppCompatTextView placeholderAppCompatTextView;
        private final AppCompatTextView suffixAppCompatTextView;
        private final AppCompatTextView dataUpdateTimeAppCompatTextView;
        
        public MainActivityRealTimeViewHolder(@NonNull View inflate, boolean isDemoMode) {
            super(inflate);
            prefixAppCompatTextView = inflate.findViewById(R.id.prefixAppCompatTextView);
            placeholderAppCompatTextView = inflate.findViewById(R.id.placeholderAppCompatTextView);
            suffixAppCompatTextView = inflate.findViewById(R.id.suffixAppCompatTextView);
            dataUpdateTimeAppCompatTextView = inflate.findViewById(R.id.dataUpdateTimeAppCompatTextView);
            MaterialCardView materialCardView = itemView.findViewById(R.id.showRealTimeDataCard);
            
            // 预览模式(设置界面展示)
            if (isDemoMode) {
                return;
            }
            
            // 点击卡片
            materialCardView.setOnClickListener(v -> {
                if (DebounceUtils.isDebounce()) {
                    return;
                }
                
                if (TextUtils.isEmpty(itemBean.getComment())) {
                    toast("该卡片没有备注");
                    return;
                }
                
                toast(itemBean.getComment());
            });
            
            // 卡片长按
            materialCardView.setOnLongClickListener(v -> {
                MaterialAlertDialogBuilder aboutDialog = new MaterialAlertDialogBuilder(v.getContext());
                aboutDialog.setTitle("提示");
                aboutDialog.setMessage(String.format("是否删除\"%s\"该组的全部卡片", itemBean.getComment()));
                aboutDialog.setNeutralButton("删除", (dialogInterface, i) -> {
                    // 删除
                    removeCard(itemBean);
                    
                    toast("删除成功" + getItemCount());
                });
                aboutDialog.setPositiveButton("取消", null);
                aboutDialog.show();
                
                return true;
            });
        }
        
        @SuppressLint("UseCompatLoadingForDrawables")
        private void bind(EditPlaceholderFragmentItemBean cardItemBean, boolean isBorder, float prefixTextSize, float placeholderTextSize, float suffixTextSize, float timeTextSize) {
            itemBean = cardItemBean;
            prefixAppCompatTextView.setText(cardItemBean.getPrefix());
            placeholderAppCompatTextView.setText(cardItemBean.getPlaceholder());
            suffixAppCompatTextView.setText(cardItemBean.getSuffix());
            dataUpdateTimeAppCompatTextView.setText(cardItemBean.getTime());
            
            // 设置字体大小
            if (prefixTextSize != 0 || placeholderTextSize != 0 || suffixTextSize != 0 || timeTextSize != 0) {
                prefixAppCompatTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, prefixTextSize);
                placeholderAppCompatTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, placeholderTextSize);
                suffixAppCompatTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, suffixTextSize);
                dataUpdateTimeAppCompatTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeTextSize);
            }
            
            // 打开显示边框
            if (isBorder) {
                prefixAppCompatTextView.setBackgroundResource(R.drawable.background_frame);
                placeholderAppCompatTextView.setBackgroundResource(R.drawable.background_frame);
                suffixAppCompatTextView.setBackgroundResource(R.drawable.background_frame);
                dataUpdateTimeAppCompatTextView.setBackgroundResource(R.drawable.background_frame);
            } else {
                prefixAppCompatTextView.setBackgroundResource(0);
                placeholderAppCompatTextView.setBackgroundResource(0);
                suffixAppCompatTextView.setBackgroundResource(0);
                dataUpdateTimeAppCompatTextView.setBackgroundResource(0);
            }
        }
        
        
        /**
         * 获取前缀文本的字体大小
         *
         * @return 前缀文本的字体大小
         */
        public float getPrefixTextSize() {
            return prefixAppCompatTextView.getTextSize();
        }
        
        /**
         * 获取占位符文本的字体大小
         *
         * @return 占位符文本的字体大小
         */
        public float getPlaceholderTextSize() {
            return placeholderAppCompatTextView.getTextSize();
        }
        
        /**
         * 获取后缀文本的字体大小
         *
         * @return 后缀文本的字体大小
         */
        public float getSuffixTextSize() {
            return suffixAppCompatTextView.getTextSize();
        }
        
        /**
         * 获取时间文本的字体大小
         *
         * @return 时间文本的字体大小
         */
        public float getTimeTextSize() {
            return dataUpdateTimeAppCompatTextView.getTextSize();
        }
        
        
    }
    
    /**
     * AsyncDiffUtil
     */
    private static final DiffUtil.ItemCallback<EditPlaceholderFragmentItemBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<EditPlaceholderFragmentItemBean>() {
        @Override
        public boolean areItemsTheSame(@NonNull EditPlaceholderFragmentItemBean oldItem, @NonNull EditPlaceholderFragmentItemBean newItem) {
            return TextUtils.equals(oldItem.getPrefix(), newItem.getPrefix()) &&
                           TextUtils.equals(oldItem.getPlaceholder(), newItem.getPlaceholder()) &&
                           TextUtils.equals(oldItem.getSuffix(), newItem.getSuffix()) &&
                           TextUtils.equals(oldItem.getComment(), newItem.getComment()) &&
                           TextUtils.equals(oldItem.getTime(), newItem.getTime());
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull EditPlaceholderFragmentItemBean oldItem, @NonNull EditPlaceholderFragmentItemBean newItem) {
            return oldItem.equals(newItem);
        }
    };
    
    /**
     * Toast
     *
     * @param content 提示内容
     */
    private void toast(Object content) {
        Toast.makeText(context, content.toString(), Toast.LENGTH_SHORT).show();
    }
    
}


