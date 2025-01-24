package cc.qi7.esp8266_test.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.bean.CardItemBean;

/**
 * @author AHeng
 * @date 2024年8月13日18:39:52
 * @status ok
 * @description 注意: submitList(list)的时候, list传入的对象不能是同一个, 每次submitList传入的list必须重新new
 */
public class PlaceholderMapperFragmentRecyclerAsyncDiffAdapter extends ListAdapter<CardItemBean, PlaceholderMapperFragmentRecyclerAsyncDiffAdapter.MyViewHolder> {
    
    private Context context;
    
    public PlaceholderMapperFragmentRecyclerAsyncDiffAdapter() {
        super(new MyAsyncDiffItemCallback());
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_fragment_placeholder_mapper_recyclerview, parent, false);
        return new MyViewHolder(view, this);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // 绑定数据到ViewHolder
        holder.bind(getItem(position));
    }
    
    /**
     * 用于比较两个CardItemBean对象是否相等的回调接口。
     */
    public static class MyAsyncDiffItemCallback extends DiffUtil.ItemCallback<CardItemBean> {
        /**
         * 判断两个CardItemBean对象是否具有相同的标识符。
         *
         * @param oldItem 旧的数据项
         * @param newItem 新的数据项
         * @return 如果两个数据项具有相同的标识符，则返回true；否则返回false。
         */
        @Override
        public boolean areItemsTheSame(@NonNull CardItemBean oldItem, @NonNull CardItemBean newItem) {
            return TextUtils.equals(oldItem.getPlaceholder(), newItem.getPlaceholder()) &&
                           TextUtils.equals(oldItem.getPrefix(), newItem.getPrefix()) &&
                           TextUtils.equals(oldItem.getSuffix(), newItem.getSuffix()) &&
                           TextUtils.equals(oldItem.getComment(), newItem.getComment());
        }
        
        /**
         * 判断两个CardItemBean对象的内容是否相同。
         *
         * @param oldItem 旧的数据项
         * @param newItem 新的数据项
         * @return 如果两个数据项的内容完全相同，则返回true；否则返回false。
         */
        @Override
        public boolean areContentsTheSame(@NonNull CardItemBean oldItem, @NonNull CardItemBean newItem) {
            return oldItem.equals(newItem);
        }
    }
    
    /**
     * 保存控件对象
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CardItemBean cardItemBean;
        // private MaterialCardView editDataCard;
        private TextInputLayout prefixTextInputLayout, suffixTextInputLayout, commentTextInputLayout;
        private EditText prefixTextInputLayoutEditText, suffixTextInputLayoutEditText, commentTextInputLayoutEditText;
        private AppCompatTextView placeholder;
        // 用于监听文本变化
        private TextWatcher prefixTextInputLayoutTextWatcher, suffixTextInputLayoutTextWatcher, commentTextInputLayoutTextWatcher;
        PlaceholderMapperFragmentRecyclerAsyncDiffAdapter diffAdapter;
        
        public MyViewHolder(View view, PlaceholderMapperFragmentRecyclerAsyncDiffAdapter diffAdapter) {
            super(view);
            this.diffAdapter = diffAdapter;
            
            // editDataCard = view.findViewById(R.id.editDataCard);
            prefixTextInputLayout = view.findViewById(R.id.prefixTextInputLayout);
            placeholder = view.findViewById(R.id.placeholder);
            suffixTextInputLayout = view.findViewById(R.id.suffixTextInputLayout);
            commentTextInputLayout = view.findViewById(R.id.commentTextInputLayout);
            
            // item点击事件
            // editDataCard.setOnClickListener(v -> {
            //     toast("点击了第" + getAdapterPosition() + "个item");
            // });
            
            prefixTextInputLayoutTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cardItemBean.setPrefix(s.toString());
                    diffAdapter.notifyItemChanged(getAdapterPosition());
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            
            
            suffixTextInputLayoutTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cardItemBean.setSuffix(s.toString());
                    diffAdapter.notifyItemChanged(getAdapterPosition());
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                
                }
            };
            
            commentTextInputLayoutTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
                }
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cardItemBean.setComment(s.toString());
                    diffAdapter.notifyItemChanged(getAdapterPosition());
                }
                
                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            
        }
        
        public void bind(CardItemBean cardItemBean) {
            this.cardItemBean = cardItemBean;
            
            // item前缀输入框
            prefixTextInputLayoutEditText = prefixTextInputLayout.getEditText();
            prefixTextInputLayoutEditText.removeTextChangedListener(prefixTextInputLayoutTextWatcher);
            prefixTextInputLayoutEditText.addTextChangedListener(prefixTextInputLayoutTextWatcher);
            
            // item后缀输入框
            suffixTextInputLayoutEditText = suffixTextInputLayout.getEditText();
            suffixTextInputLayoutEditText.removeTextChangedListener(suffixTextInputLayoutTextWatcher);
            suffixTextInputLayoutEditText.addTextChangedListener(suffixTextInputLayoutTextWatcher);
            
            // item备注输入框
            commentTextInputLayoutEditText = commentTextInputLayout.getEditText();
            commentTextInputLayoutEditText.removeTextChangedListener(commentTextInputLayoutTextWatcher);
            commentTextInputLayoutEditText.addTextChangedListener(commentTextInputLayoutTextWatcher);
            
            // 设置占位符
            placeholder.setText(cardItemBean.getPlaceholder());
            
        }
    }
    
}

