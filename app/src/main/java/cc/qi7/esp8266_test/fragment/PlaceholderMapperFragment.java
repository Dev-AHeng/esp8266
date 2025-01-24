package cc.qi7.esp8266_test.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.adapter.PlaceholderMapperFragmentRecyclerAsyncDiffAdapter;
import cc.qi7.esp8266_test.bean.CardItemBean;
import cc.qi7.esp8266_test.viewmodel.FirstFragmentViewModel;
import cc.qi7.esp8266_test.viewmodel.SecondFragmentViewModel;
import cc.qi7.esp8266_test.viewmodel.ThirdFragmentViewModel;

/**
 * @author AHeng
 * @date 2024/08/11 23:59
 */
public class PlaceholderMapperFragment extends Fragment {
    private static final String TAG = "日志";
    private final ViewPager2 viewPager2;
    private View rootView;
    private PlaceholderMapperFragmentRecyclerAsyncDiffAdapter recyclerAsyncDiffAdapter;
    private FirstFragmentViewModel firstFragmentViewModel;
    private SecondFragmentViewModel secondFragmentViewModel;
    private ThirdFragmentViewModel thirdFragmentViewModel;
    private List<CardItemBean> returnCardItemBeanList;
    private RecyclerView recyclerView;
    
    private boolean isFirst = true;
    
    public PlaceholderMapperFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_placeholder_mapper, container, false);
            recyclerView = rootView.findViewById(R.id.recyclerView);
            // 调用 setHasFixedSize(true) 来设置
            recyclerView.setHasFixedSize(true);
            RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            // 取消item局部更新烁动画
            ((DefaultItemAnimator) Objects.requireNonNull(itemAnimator)).setSupportsChangeAnimations(false);
            // 设置xml动画
            // recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down));
            // 设置第三方动画
            // recyclerView.setItemAnimator(new SlideInDownAnimator());
            // itemAnimator.setChangeDuration(300L);
            // itemAnimator.setRemoveDuration(300L);
            // itemAnimator.setAddDuration(300L);
            // itemAnimator.setMoveDuration(300L);
            // 设置布局管理器
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            // 设置适配器
            recyclerAsyncDiffAdapter = new PlaceholderMapperFragmentRecyclerAsyncDiffAdapter();
            recyclerView.setAdapter(recyclerAsyncDiffAdapter);
            List<CardItemBean> aa = new ArrayList<>();
            recyclerAsyncDiffAdapter.submitList(aa);
            
            // viewModel通讯
            firstFragmentViewModel = new ViewModelProvider(requireActivity()).get(FirstFragmentViewModel.class);
            secondFragmentViewModel = new ViewModelProvider(requireActivity()).get(SecondFragmentViewModel.class);
            thirdFragmentViewModel = new ViewModelProvider(requireActivity()).get(ThirdFragmentViewModel.class);
            
            // 上一页
            rootView.findViewById(R.id.prevPageButton).setOnClickListener(v -> {
                // 发送cardItemBeanList给第一个Fragment
                firstFragmentViewModel.sendToFirstFragment(recyclerAsyncDiffAdapter.getCurrentList());
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
            });
            
            // 下一页
            rootView.findViewById(R.id.nextPageButton).setOnClickListener(v -> {
                // 发送cardItemBeanList给第三个Fragment
                thirdFragmentViewModel.sendToThirdFragment(recyclerAsyncDiffAdapter.getCurrentList());
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            });
            
            // 接收第一个Fragment发过来的cardItemBeanList
            secondFragmentViewModel.getMessage().observe(getViewLifecycleOwner(), returnCardItemBeanList -> {
                this.returnCardItemBeanList = returnCardItemBeanList;
                Log.i(TAG, "接收第一个Fragment发过来的cardItemBeanList: " + returnCardItemBeanList);
            });
            
        }
        return rootView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 设置RecyclerView Item
        recyclerAsyncDiffAdapter.submitList(returnCardItemBeanList);
        
        // 是否第一次执行
        if (isFirst) {
            isFirst = false;
            return;
        }
        requestFocusAndShowKeyboard(recyclerView);
    }
    
    /**
     * 显示软键盘
     *
     * @param view View
     */
    private void requestFocusAndShowKeyboard(final View view) {
        // 请求焦点
        view.post(() -> {
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
            
            // hideKeyboard();
        });
    }
    
    /**
     * 收起软键盘
     */
    public void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
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
