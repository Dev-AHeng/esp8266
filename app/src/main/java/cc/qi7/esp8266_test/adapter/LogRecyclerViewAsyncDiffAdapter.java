package cc.qi7.esp8266_test.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.activity.BaseActivity;
import cc.qi7.esp8266_test.bean.LogItemBean;
import cc.qi7.esp8266_test.utils.ClipboardHelper;
import cc.qi7.esp8266_test.utils.Clone;

/**
 * @author AHeng
 * @date 2024/08/25 23:39
 */
public class LogRecyclerViewAsyncDiffAdapter extends ListAdapter<LogItemBean, LogRecyclerViewAsyncDiffAdapter.MyViewHolder> {
    private static final String TAG = "日志";
    
    public LogRecyclerViewAsyncDiffAdapter() {
        super(DIFF_CALLBACK);
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawer_log, parent, false);
        return new MyViewHolder(inflate);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    
    @Override
    public void submitList(@Nullable List<LogItemBean> list) {
        // 判断list超过80条, 就从表头删除一条
        if (list != null && list.size() > 100) {
            list.remove(0);
        }
        
        // 深度拷贝s
        try {
            list = Clone.deepCopyList(list);
        } catch (IOException |
                 ClassNotFoundException e) {
            Log.i(TAG, "深拷贝异常" + e);
            throw new RuntimeException(e);
        }
        
        super.submitList(list);
    }
    
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView starName;
        private final ImageView starAvatar;
        private final TextView starMessage;
        private final TextView endName;
        private final ImageView endAvatar;
        private final TextView endMessage;
        
        private LogItemBean logItemBean;
        
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ConstraintLayout drawerLogItem = itemView.findViewById(R.id.drawer_log_item);
            starName = itemView.findViewById(R.id.star_name);
            starAvatar = itemView.findViewById(R.id.star_avatar);
            starMessage = itemView.findViewById(R.id.star_message);
            endName = itemView.findViewById(R.id.end_name);
            endAvatar = itemView.findViewById(R.id.end_avatar);
            endMessage = itemView.findViewById(R.id.end_message);
            
            // 点击复制
            drawerLogItem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ClipboardHelper.copyToClipboard(itemView.getContext(), logItemBean.toString());
                }
            });
            
            // 长按item分享
            BaseActivity baseActivity = new BaseActivity();
            drawerLogItem.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String content = baseActivity.paramsToString(logItemBean.getRole().getLeftOrRight(), logItemBean.getRole().getName(), logItemBean.getTime(), logItemBean.getMessage());
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, content);
                    shareIntent.setType("text/plain");
                    Intent.createChooser(shareIntent, "分享到");
                    itemView.getContext().startActivity(shareIntent);
                }
                return true;
            });
            
        }
        
        @SuppressLint("SetTextI18n")
        public void bind(LogItemBean logItemBean) {
            this.logItemBean = logItemBean;
            if (logItemBean.getRole().getLeftOrRight()) {
                // 我方
                endName.setText(logItemBean.getTime() + " " + logItemBean.getRole().getName());
                endMessage.setText(logItemBean.getMessage());
                
                // 隐藏
                starName.setVisibility(View.GONE);
                starAvatar.setVisibility(View.GONE);
                starMessage.setVisibility(View.GONE);
                
                // 显示
                endName.setVisibility(View.VISIBLE);
                endAvatar.setVisibility(View.VISIBLE);
                endMessage.setVisibility(View.VISIBLE);
            } else {
                if (logItemBean.getRole().getName().equals(LogItemBean.CLOUD)) {
                    // 云端
                    starAvatar.setImageResource(R.drawable.baseline_filter_drama_24);
                } else if (logItemBean.getRole().getName().equals(LogItemBean.APP_SYSTEM_INFO)) {
                    // APP系统信息
                    starAvatar.setImageResource(R.drawable.baseline_phone_android_24);
                }
                
                starName.setText(logItemBean.getRole().getName() + " " + logItemBean.getTime());
                starMessage.setText(logItemBean.getMessage());
                
                // 隐藏
                endName.setVisibility(View.GONE);
                endAvatar.setVisibility(View.GONE);
                endMessage.setVisibility(View.GONE);
                
                // 显示
                starName.setVisibility(View.VISIBLE);
                starAvatar.setVisibility(View.VISIBLE);
                starMessage.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private static final DiffUtil.ItemCallback<LogItemBean> DIFF_CALLBACK = new DiffUtil.ItemCallback<LogItemBean>() {
        @Override
        public boolean areItemsTheSame(@NonNull LogItemBean oldItem, @NonNull LogItemBean newItem) {
            return oldItem.getRole().getLeftOrRight() == newItem.getRole().getLeftOrRight() &&
                           TextUtils.equals(oldItem.getRole().getName(), newItem.getRole().getName()) &&
                           TextUtils.equals(oldItem.getTime(), newItem.getTime()) &&
                           TextUtils.equals(oldItem.getMessage(), newItem.getMessage());
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull LogItemBean oldItem, @NonNull LogItemBean newItem) {
            return oldItem.equals(newItem);
        }
    };
    
}
