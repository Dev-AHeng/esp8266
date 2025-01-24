package cc.qi7.esp8266_test.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.bean.CardItemBean;
import cc.qi7.esp8266_test.utils.DebounceUtils;
import cc.qi7.esp8266_test.viewmodel.FirstFragmentViewModel;
import cc.qi7.esp8266_test.viewmodel.SecondFragmentViewModel;

/**
 * @author AHeng
 * @date 2024/08/11 23:55
 */
public class EditPlaceholderFragment extends Fragment {
    private static final String TAG = "日志";
    private View rootView;
    /**
     * EditText hint
     */
    private static final String EXAMPLE_HINT = "{\n  \"id\": \"abc123\",\n" + "  \"params\": {\n" + "    \"LightSwitch\": 1,\n" + "    \"temperature\": 38.86,\n" + "  },\n" + "  \"method\": \"thing.event.property.post\"\n" + "}\n" + "// 上面是数据源(也就是app这边接收到硬件设备发过来的数据), 下面举个例子怎么标记占位符:\n" + "{\n" + "  \"id\": \"@1@\",\n" + "  \"params\": {\n" + "    \"lightSwitch\": @2@,\n" + "    \"temperature\": @3@,\n" + "  },\n" + "  \"method\": \"thing.event.property.post\"\n" + "}\n" + "// 总结, 就是把所有动态的会变的内容用@1@, @2@, ..., @x@代替掉。";
    private static final String REGEX = "@\\d+@";
    private TextInputLayout modelStringTextInputLayout;
    private final ViewPager2 viewPager2;
    private List<CardItemBean> cardItemBeans;
    private final List<CardItemBean> returnBeans = new ArrayList<>();
    
    private FirstFragmentViewModel firstFragmentViewModel;
    private SecondFragmentViewModel secondFragmentViewModel;
    
    public EditPlaceholderFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_edit_placeholder, container, false);
            // 模版字符串输入框
            modelStringTextInputLayout = rootView.findViewById(R.id.modelStringTextInputLayout);
            // 动态设置hint
            Objects.requireNonNull(modelStringTextInputLayout.getEditText()).setHint(EXAMPLE_HINT);
            // 在视图绘制完成后请求焦点
            modelStringTextInputLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    modelStringTextInputLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    modelStringTextInputLayout.requestFocus();
                    return true;
                }
            });
            
            Pattern pattern = Pattern.compile(REGEX);
            
            // editPlaceholderNextPageButton.setOnClickListener(view -> {
            //
            //     List<CardItemBean> qq = new ArrayList<>();
            //     CardItemBean qqcardItemBean1 = new CardItemBean();
            //     qqcardItemBean1.setPrefix("11");
            //     qqcardItemBean1.setSuffix("11");
            //     qqcardItemBean1.setPlaceholder("@1@");
            //     qqcardItemBean1.setComment("11");
            //
            //     CardItemBean qqcardItemBean2 = new CardItemBean();
            //     qqcardItemBean2.setPrefix("22");
            //     qqcardItemBean2.setSuffix("22");
            //     qqcardItemBean2.setPlaceholder("@2@");
            //     qqcardItemBean2.setComment("22");
            //
            //     CardItemBean qqcardItemBean3 = new CardItemBean();
            //     qqcardItemBean3.setPrefix("33");
            //     qqcardItemBean3.setSuffix("33");
            //     qqcardItemBean3.setPlaceholder("@3@");
            //     qqcardItemBean3.setComment("33");
            //
            //     CardItemBean qqcardItemBean4 = new CardItemBean();
            //     qqcardItemBean4.setPrefix("44");
            //     qqcardItemBean4.setSuffix("44");
            //     qqcardItemBean4.setPlaceholder("@4@");
            //     qqcardItemBean4.setComment("44");
            //
            //     qq.add(qqcardItemBean1);
            //     qq.add(qqcardItemBean2);
            //     qq.add(qqcardItemBean3);
            //     qq.add(qqcardItemBean4);
            //
            //
            //     viewPager2.setTag(qq);
            //     viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            // });
            //
            // editPlaceholderNextPageButton.setOnLongClickListener(view -> {
            //
            //     List<CardItemBean> qq = new ArrayList<>();
            //     CardItemBean cardItemBean1 = new CardItemBean();
            //     cardItemBean1.setPrefix("11");
            //     cardItemBean1.setSuffix("11");
            //     cardItemBean1.setPlaceholder("@1@");
            //     cardItemBean1.setComment("11");
            //
            //     CardItemBean cardItemBean2 = new CardItemBean();
            //     // cardItemBean2.setPrefix("22");
            //     // cardItemBean2.setSuffix("22");
            //     cardItemBean2.setPlaceholder("@3@");
            //     // cardItemBean2.setComment("22");
            //
            //     CardItemBean cardItemBean3 = new CardItemBean();
            //     // cardItemBean3.setPrefix("33");
            //     // cardItemBean3.setSuffix("33");
            //     cardItemBean3.setPlaceholder("@2@");
            //     // cardItemBean3.setComment("33");
            //
            //     CardItemBean cardItemBean4 = new CardItemBean();
            //     cardItemBean4.setPrefix("44");
            //     cardItemBean4.setSuffix("44");
            //     cardItemBean4.setPlaceholder("@4@");
            //     cardItemBean4.setComment("44");
            //
            //     qq.add(cardItemBean1);
            //     qq.add(cardItemBean2);
            //     qq.add(cardItemBean3);
            //     qq.add(cardItemBean4);
            //
            //
            //     viewPager2.setTag(qq);
            //     viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            //     return false;
            // });
            
            firstFragmentViewModel = new ViewModelProvider(requireActivity()).get(FirstFragmentViewModel.class);
            secondFragmentViewModel = new ViewModelProvider(requireActivity()).get(SecondFragmentViewModel.class);
            
            // 下一页按钮
            rootView.findViewById(R.id.editPlaceholderNextPageButton).setOnClickListener(view -> {
                EditText editText = modelStringTextInputLayout.getEditText();
                String editContent = editText.getText().toString().replaceAll("\\s+", "");
                
                // 输入框判空
                if (editContent.trim().isEmpty()) {
                    editText.setError("必填的");
                    return;
                }
                
                // 收集占位符
                cardItemBeans = new ArrayList<>();
                Matcher matcher = pattern.matcher(editContent);
                while (matcher.find()) {
                    CardItemBean cardItemBean = new CardItemBean();
                    cardItemBean.setPlaceholderModelContent(editContent);
                    cardItemBean.setPlaceholderSplit(editContent.split(REGEX));
                    cardItemBean.setPlaceholder(matcher.group());
                    cardItemBeans.add(cardItemBean);
                }
                
                Log.i(TAG, cardItemBeans.toString());
                
                // 验证是否设置占位符
                if (cardItemBeans.isEmpty()) {
                    editText.setError("请设置占位符");
                    return;
                }
                
                // 判断是否有重复
                if (isDuplicates(cardItemBeans)) {
                    editText.setError("占位符重复了");
                    return;
                }
                
                // 判断占位符是否连续
                for (int index = 1; index <= cardItemBeans.size(); index++) {
                    if (!editContent.contains("@" + index + "@")) {
                        toast("占位符索引从@1@开始且连续");
                        return;
                    }
                }
                
                // 第二个Fragment数据环回赋值
                if (!returnBeans.isEmpty()) {
                    for (CardItemBean cardItemBean : cardItemBeans) {
                        for (CardItemBean returnBean : returnBeans) {
                            if (cardItemBean.getPlaceholder().equals(returnBean.getPlaceholder())) {
                                cardItemBean.setComment(returnBean.getComment());
                                cardItemBean.setPrefix(returnBean.getPrefix());
                                cardItemBean.setSuffix(returnBean.getSuffix());
                            }
                        }
                    }
                }
                
                // 收起键盘
                // hideKeyboard();
                
                // 带着模版字符串, 转跳到下一个Fragment
                secondFragmentViewModel.sendToSecondFragment(cardItemBeans);
                // viewPager2.setTag(cardItemBeans);
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            });
            
            // 接收第二个Fragment发过来的CardItemBeanList
            firstFragmentViewModel.getMessage().observe(getViewLifecycleOwner(), returnCardItemBeanList -> {
                Log.i(TAG, "接收第二个Fragment发过来的CardItemBeanList: " + returnCardItemBeanList);
                returnBeans.addAll(returnCardItemBeanList);
            });
            
        }
        return rootView;
    }
    
    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }
    
    /**
     * 判断列表中是否有重复元素
     *
     * @param sourceList List obj
     * @return 如果add()返回false，说明元素已存在，即有重复
     */
    private boolean isDuplicates(List<CardItemBean> sourceList) {
        HashSet<CardItemBean> stringSet = new HashSet<>(sourceList);
        return sourceList.size() != stringSet.size();
    }
    
    /**
     * Toast
     *
     * @param content 提示内容
     */
    public void toast(Object content) {
        Toast.makeText(requireContext(), content.toString(), Toast.LENGTH_SHORT).show();
    }
    
}
