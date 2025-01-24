package cc.qi7.esp8266_test.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.adapter.PreviewCardFragmentRecyclerAsyncDiffAdapter;
import cc.qi7.esp8266_test.bean.CardGroupBean;
import cc.qi7.esp8266_test.bean.CardItemBean;
import cc.qi7.esp8266_test.bean.EditPlaceholderFragmentItemBean;
import cc.qi7.esp8266_test.config.PublicConfig;
import cc.qi7.esp8266_test.utils.DateTimeUtil;
import cc.qi7.esp8266_test.viewmodel.ThirdFragmentViewModel;

/**
 * @author AHeng
 * @date 2024/08/15 15:14
 */
public class PreviewCardFragment extends Fragment {
    private static final String TAG = "日志";
    private View rootView;
    private final ViewPager2 viewPager2;
    private PreviewCardFragmentRecyclerAsyncDiffAdapter recyclerAsyncDiffAdapter;
    private List<CardItemBean> newCardItemBeanList;
    
    public PreviewCardFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_preview_card, container, false);
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            ((DefaultItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            recyclerAsyncDiffAdapter = new PreviewCardFragmentRecyclerAsyncDiffAdapter();
            recyclerView.setAdapter(recyclerAsyncDiffAdapter);
            
            // 接收第二个Fragment发来的cardItemBeanList
            ThirdFragmentViewModel thirdFragmentViewModel = new ViewModelProvider(requireActivity()).get(ThirdFragmentViewModel.class);
            thirdFragmentViewModel.getMessage().observe(getViewLifecycleOwner(), returnCardItemBeanList -> {
                newCardItemBeanList = returnCardItemBeanList;
            });
            
            // 上一页
            rootView.findViewById(R.id.prevPageButton).setOnClickListener(v -> {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
            });
            
            // 完成
            rootView.findViewById(R.id.completeButton).setOnClickListener(v -> {
                List<CardItemBean> currentList = recyclerAsyncDiffAdapter.getCurrentList();
                
                // 构造MainActivity实时卡片Bean
                Map<String, EditPlaceholderFragmentItemBean> cardItemBeanMap = new LinkedHashMap<>();
                for (CardItemBean cardItemBean : currentList) {
                    cardItemBeanMap.put(cardItemBean.getPlaceholder(), new EditPlaceholderFragmentItemBean(cardItemBean.getPrefix(), PublicConfig.NO_DATA, cardItemBean.getSuffix(), cardItemBean.getComment(), DateTimeUtil.getCurrentFormattedTime()));
                }
                CardGroupBean cardGroupBean = new CardGroupBean();
                cardGroupBean.setPlaceholderModelContent(currentList.get(0).getPlaceholderModelContent());
                cardGroupBean.setPlaceholderSplit(currentList.get(0).getPlaceholderSplit());
                cardGroupBean.setCardItemBeanMap(cardItemBeanMap);
                
                // 返回数据给启动的Activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PublicConfig.SERIALIZABLE_EXTRA_KEY, cardGroupBean);
                requireActivity().setResult(Activity.RESULT_OK, returnIntent);
                
                requireActivity().finish();
            });
            
            
            // 添加item测试
            // rootView.findViewById(R.id.completeButton).setOnLongClickListener(v -> {
            //
            //     // cardItemBeanList = new ArrayList<>();
            //     //
            //     // CardItemBean cardItemBean1 = new CardItemBean();
            //     // cardItemBean1.setPlaceholderModelContent("[{\"id\":\", \",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}]");
            //     // cardItemBean1.setPlaceholderSplit(new String[] {"固定的1", "固定的2", "固定的3"});
            //     // cardItemBean1.setPrefix("111");
            //     // cardItemBean1.setPlaceholder("111");
            //     // cardItemBean1.setSuffix("111");
            //     // cardItemBean1.setComment("111");
            //     //
            //     // CardItemBean cardItemBean2 = new CardItemBean();
            //     // cardItemBean2.setPlaceholderModelContent("[{\"id\":\", \",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}]");
            //     // cardItemBean2.setPlaceholderSplit(new String[] {"固定的1", "固定的2", "固定的3"});
            //     // cardItemBean2.setPrefix("222");
            //     // cardItemBean2.setPlaceholder("222");
            //     // cardItemBean2.setSuffix("222");
            //     // cardItemBean2.setComment("222");
            //     //
            //     // CardItemBean cardItemBean3 = new CardItemBean();
            //     // cardItemBean3.setPlaceholderModelContent("[{\"id\":\", \",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}]");
            //     // cardItemBean3.setPlaceholderSplit(new String[] {"固定的1", "固定的2", "固定的3"});
            //     // cardItemBean3.setPrefix("333");
            //     // cardItemBean3.setPlaceholder("333");
            //     // cardItemBean3.setSuffix("333");
            //     // cardItemBean3.setComment("333");
            //     //
            //     // cardItemBeanList.add(cardItemBean1);
            //     // cardItemBeanList.add(cardItemBean3);
            //     // cardItemBeanList.add(cardItemBean2);
            //     //
            //     // Log.i(TAG, "点击:: " + cardItemBeanList);
            //     //
            //     // recyclerAsyncDiffAdapter.submitList(cardItemBeanList);
            //
            //
            //     cardItemBeanList = new ArrayList<>();
            //     CardItemBean cardItemBean1 = new CardItemBean();
            //     cardItemBean1.setPlaceholderModelContent("[{\"id\":\", \",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}]");
            //     cardItemBean1.setPlaceholderSplit(new String[] {"固定的1", "固定的2", "固定的3"});
            //     cardItemBean1.setPrefix("hah");
            //     cardItemBean1.setPlaceholder("haa");
            //     cardItemBean1.setSuffix("hah");
            //     cardItemBean1.setComment("hah");
            //
            //     CardItemBean cardItemBean2 = new CardItemBean();
            //     cardItemBean2.setPlaceholderModelContent("[{\"id\":\", \",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}]");
            //     cardItemBean2.setPlaceholderSplit(new String[] {"固定的1", "固定的2", "固定的3"});
            //     cardItemBean2.setPrefix("哈哈");
            //     cardItemBean2.setPlaceholder("222");
            //     cardItemBean2.setSuffix("222");
            //     cardItemBean2.setComment("222");
            //
            //     CardItemBean cardItemBean3 = new CardItemBean();
            //     cardItemBean3.setPlaceholderModelContent("[{\"id\":\", \",\"params\":{\"lightSwitch\":@3@,\"temperature\":@1@,\"test\":\"@2@\"},\"method\":\"@4@\"}]");
            //     cardItemBean3.setPlaceholderSplit(new String[] {"固定的1", "固定的2", "固定的3"});
            //     cardItemBean3.setPrefix("333");
            //     cardItemBean3.setPlaceholder("333");
            //     cardItemBean3.setSuffix("333");
            //     cardItemBean3.setComment("333");
            //
            //     cardItemBeanList.add(cardItemBean3);
            //     cardItemBeanList.add(cardItemBean1);
            //     cardItemBeanList.add(cardItemBean2);
            //
            //
            //     Log.i(TAG, "长按:: " + cardItemBeanList);
            //
            //     recyclerAsyncDiffAdapter.submitList(cardItemBeanList);
            //
            //     toast("长按");
            //
            //     return true;
            // });
            
        }
        
        return rootView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        recyclerAsyncDiffAdapter.submitList(newCardItemBeanList);
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
