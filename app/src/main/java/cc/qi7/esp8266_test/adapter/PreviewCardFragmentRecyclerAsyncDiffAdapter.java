package cc.qi7.esp8266_test.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.bean.CardItemBean;
import cc.qi7.esp8266_test.utils.Clone;

/**
 * @author AHeng
 * @date 2024/08/15 22:45
 */
public class PreviewCardFragmentRecyclerAsyncDiffAdapter extends ListAdapter<CardItemBean, PreviewCardFragmentRecyclerAsyncDiffAdapter.ViewHolder> {
    private static final String TAG = "日志";
    private Context context;
    
    public PreviewCardFragmentRecyclerAsyncDiffAdapter() {
        super(new AsyncDiffItemCallback());
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_preview_card_fragment_recycleview, parent, false);
        return new ViewHolder(inflate);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
    
    @Override
    public void submitList(@Nullable List<CardItemBean> list) {
        try {
            // 深度拷贝
            list = Clone.deepCopyList(list);
        } catch (IOException |
                 ClassNotFoundException e) {
            Log.i(TAG, "深拷贝异常" + e);
            throw new RuntimeException(e);
        }
        
        super.submitList(list);
    }
    
    public static class AsyncDiffItemCallback extends DiffUtil.ItemCallback<CardItemBean> {
        @Override
        public boolean areItemsTheSame(@NonNull CardItemBean oldItem, @NonNull CardItemBean newItem) {
            // 首先比较最基本的唯一标识符
            if (!TextUtils.equals(oldItem.getPrefix(), newItem.getPrefix())) {
                return false;
            }
            
            if (!TextUtils.equals(oldItem.getPlaceholder(), newItem.getPlaceholder())) {
                return false;
            }
            
            return TextUtils.equals(oldItem.getSuffix(), newItem.getSuffix());
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull CardItemBean oldItem, @NonNull CardItemBean newItem) {
            return oldItem.equals(newItem);
            // return TextUtils.equals(oldItem.getPrefix(), newItem.getPrefix())
            //                && TextUtils.equals(oldItem.getPlaceholder(), newItem.getPlaceholder())
            //                && TextUtils.equals(oldItem.getSuffix(), newItem.getSuffix())
            //                && TextUtils.equals(oldItem.getComment(), newItem.getComment());
        }
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView prefixAppCompatTextView;
        private final AppCompatTextView placeholderAppCompatTextView;
        private final AppCompatTextView suffixAppCompatTextView;
        
        public ViewHolder(View inflate) {
            super(inflate);
            prefixAppCompatTextView = inflate.findViewById(R.id.prefixAppCompatTextView);
            placeholderAppCompatTextView = inflate.findViewById(R.id.placeholderAppCompatTextView);
            suffixAppCompatTextView = inflate.findViewById(R.id.suffixAppCompatTextView);
            
        }
        
        public void bind(CardItemBean cardItemBean) {
            prefixAppCompatTextView.setText(cardItemBean.getPrefix());
            placeholderAppCompatTextView.setText(cardItemBean.getPlaceholder());
            suffixAppCompatTextView.setText(cardItemBean.getSuffix());
        }
    }
    
    /**
     * Toast
     *
     * @param content 提示内容
     */
    public void toast(Object content) {
        Toast.makeText(context, content.toString(), Toast.LENGTH_SHORT).show();
    }
    
    
}
