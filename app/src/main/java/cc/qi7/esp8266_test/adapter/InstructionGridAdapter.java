package cc.qi7.esp8266_test.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.gson.Gson;

import java.util.List;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.bean.GridViewItemBean;

/**
 * @author AHeng
 * @date 2024年8月5日00:2:12
 */
public class InstructionGridAdapter extends BaseAdapter {
    private static final String TAG = "日志";
    
    private Context context;
    private List<GridViewItemBean> gridViewItemModels;
    public InstructionEventListener instructionEventListener;
    
    public InstructionGridAdapter(Context context, List<GridViewItemBean> gridViewItemModels) {
        this.context = context;
        this.gridViewItemModels = gridViewItemModels;
    }
    
    @Override
    public int getCount() {
        return gridViewItemModels.size();
    }
    
    @Override
    public Object getItem(int position) {
        return gridViewItemModels.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        GridViewItemBean gridViewItemBean = gridViewItemModels.get(position);
        
        if (gridViewItemBean.getWidgetType().contains("button")) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_button, parent, false);
            
            MaterialButton materialButton = convertView.findViewById(R.id.gridViewModelButton);
            materialButton.setText(gridViewItemBean.getWidgetText());
            materialButton.setBackgroundColor(R.color.purple_200);
            
            // 点击事件
            materialButton.setOnClickListener(v -> instructionEventListener.onButtonClick(gridViewItemBean, v));
            
            // 长按事件
            materialButton.setOnLongClickListener(v -> {
                instructionEventListener.onButtonLongClick(this, position);
                return true;
            });
            
        }
        
        if (gridViewItemBean.getWidgetType().contains("switch")) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_switch, parent, false);
            
            MaterialSwitch materialSwitch = convertView.findViewById(R.id.gridViewModelSwitch);
            materialSwitch.setText(gridViewItemBean.getWidgetText());
            materialSwitch.setChecked(gridViewItemBean.getWitchStatus());
            
            // 点击事件
            materialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                gridViewItemBean.setWitchStatus(isChecked);
                instructionEventListener.onSwitchClick(gridViewItemBean, isChecked);
            });
            
            // 长按事件
            materialSwitch.setOnLongClickListener(v -> {
                instructionEventListener.onSwitchLongClick(this, position);
                return true;
            });
        }
        
        return convertView;
    }
    
    public static class InstructionEventListener {
        public void onButtonClick(GridViewItemBean gridViewItemBean, View widgetView) {
        
        }
        
        public void onSwitchClick(GridViewItemBean gridViewItemBean, boolean isChecked) {
        
        }
        
        public void onButtonLongClick(InstructionGridAdapter instructionGridAdapter, int position) {
        
        }
        
        public void onSwitchLongClick(InstructionGridAdapter instructionGridAdapter, int position) {
        
        }
    }
    
    public void setInstructionEventListener(InstructionEventListener instructionEventListener) {
        this.instructionEventListener = instructionEventListener;
    }
    
    /**
     * Toast
     *
     * @param content 提示内容
     */
    public void toast(Object content) {
        Toast.makeText(context, content.toString(), Toast.LENGTH_SHORT).show();
    }
    
    
    public void addItem(GridViewItemBean gridViewItemModel) {
        gridViewItemModels.add(gridViewItemModel);
        // 刷新
        notifyDataSetChanged();
    }
    
    public void clearAll() {
        gridViewItemModels.clear();
        // 刷新
        notifyDataSetChanged();
    }
    
    public void remove(int position) {
        gridViewItemModels.remove(position);
        // 刷新
        notifyDataSetChanged();
    }
    
    public List<GridViewItemBean> getAll() {
        return gridViewItemModels;
    }
    
    // 修改指定位置的数据
    public void changeItem(int position, GridViewItemBean gridViewItemModel) {
        gridViewItemModels.set(position, gridViewItemModel);
        // 刷新
        notifyDataSetChanged();
    }
    
    public String getJson(Gson gson) {
        return gson.toJson(gridViewItemModels);
    }
    
}