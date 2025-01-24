package cc.qi7.esp8266_test.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import cc.qi7.esp8266_test.R;
import cc.qi7.esp8266_test.adapter.PlaceholderFragmentAdapter;
import cc.qi7.esp8266_test.fragment.EditPlaceholderFragment;
import cc.qi7.esp8266_test.fragment.PlaceholderMapperFragment;
import cc.qi7.esp8266_test.fragment.PreviewCardFragment;


/**
 * @author Dev_Heng
 * @date 2024年8月17日22:02:04
 */
public class CreateRealTimeCardActivity extends BaseActivity {
    private static final String TAG = "日志";
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private final List<Fragment> fragments = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_real_time_card);
        
        initView();
        
        fragments.add(new EditPlaceholderFragment(viewPager2));
        fragments.add(new PlaceholderMapperFragment(viewPager2));
        fragments.add(new PreviewCardFragment(viewPager2));
        
        //实例化适配器
        PlaceholderFragmentAdapter placeholderFragmentAdapter = new PlaceholderFragmentAdapter(getSupportFragmentManager(), getLifecycle(), fragments);
        //设置适配器
        viewPager2.setAdapter(placeholderFragmentAdapter);
        // 可以禁止vp2滑动换页
        viewPager2.setUserInputEnabled(false);
        
        // TabLayout和Viewpager2进行关联
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.baseline_looks_one_24);
                    tab.setText("标记占位符");
                    break;
                case 1:
                    tab.setIcon(R.drawable.baseline_looks_two_24);
                    tab.setText("编辑卡片");
                    break;
                case 2:
                    tab.setIcon(R.drawable.baseline_looks_3_24);
                    tab.setText("浏览效果");
                    break;
                default:
                    break;
            }
        }).attach();
        
        
        // 添加一个OnTabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当一个tab被选中时调用
                int position = tab.getPosition();
                // 你可以在这里做任何事情，比如改变ViewPager2的当前页面
                viewPager2.setCurrentItem(position);
                // log("onTabSelected: " + position);
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 当一个tab失去选中状态时调用
                // log("onTabUnselected: " + tab.getPosition());
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 当一个已经选中的tab再次被点击时调用
                // log("onTabReselected: " + tab.getPosition());
            }
        });
        
        
        // 反序列化
        // try (FileInputStream fis = openFileInput(PublicConfig.CARD_GROUP_BEAN);
        //      ObjectInputStream ois = new ObjectInputStream(fis)) {
        //     CardGroupBean localCardGroupBean = (CardGroupBean) ois.readObject();
        //     Log.i(TAG, "哈哈哈哈:: " + localCardGroupBean.toString());
        // } catch (Exception e) {
        //     throw new RuntimeException(e);
        // }
        
        // CreateRealTimeCardActivityViewModel thirdFragmentViewModel = new ViewModelProvider(this).get(CreateRealTimeCardActivityViewModel.class);
        // thirdFragmentViewModel.getMessage().observe(this, isFinish -> {
        //     if (isFinish) {
        //         finish();
        //     }
        // });
        //
    }
    
    private void initView() {
        tabLayout = findViewById(R.id.tabLayout);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        //引入toolBar
        setSupportActionBar(topAppBar);
        //toolBar 左侧返回图标事件
        topAppBar.setNavigationOnClickListener(v -> finish());
        viewPager2 = findViewById(R.id.viewPager2);
    }

    
}