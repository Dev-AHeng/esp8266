package cc.qi7.esp8266_test.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cc.qi7.esp8266_test.bean.CardItemBean;

/**
 * @author AHeng
 * @date 2024/08/15 19:51
 */
public class FirstFragmentViewModel extends ViewModel {
    private final MutableLiveData<List<CardItemBean>> mutableLiveData = new MutableLiveData<>();
    
    public MutableLiveData<List<CardItemBean>> getMessage() {
        return mutableLiveData;
    }
    
    public void sendToFirstFragment(List<CardItemBean> sendToFirstFragmentMessage) {
        mutableLiveData.setValue(sendToFirstFragmentMessage);
    }
}
