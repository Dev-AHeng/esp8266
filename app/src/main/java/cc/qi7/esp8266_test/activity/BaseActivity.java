package cc.qi7.esp8266_test.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

/**
 * Activity基类
 *
 * @author Dev_Heng
 * @date 2024年8月4日22:04:44
 * @status ok
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "日志";
    
    
    /**
     * 启动新的Activity
     *
     * @param clazz 类名
     */
    public <T extends Activity> void goToActivity(Class<T> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
    
    
    /**
     * 将不定参数方法的参数值拼接
     *
     * @param params 参数
     * @return 结果
     */
    public String paramsToString(Object... params) {
        if (params == null || params.length == 0) {
            return "";
        }
        
        // StackTraceElement stackTraceElement = new Throwable().getStackTrace()[2];
        // String invokeInfo = stackTraceElement.getFileName() + "-->" + stackTraceElement.getLineNumber();
        
        String invokeInfo = "";
        // 当前类名
        String currentClassName = "BaseActivity";
        StackTraceElement[] stackTraceElementArray = new Throwable().getStackTrace();
        for (int index = 0; index < stackTraceElementArray.length; index++) {
            String stackInfo = stackTraceElementArray[index].toString();
            if (stackInfo.contains(currentClassName)) {
                String info = stackTraceElementArray[index + 1].toString();
                if (!info.contains(currentClassName)) {
                    invokeInfo = info;
                    break;
                }
            }
        }
        
        int count = 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---------开始----------\n").append(invokeInfo);
        for (Object parma : params) {
            stringBuilder.append("\n----------").append(count++).append("-----------\n").append(parma).append("\n");
        }
        stringBuilder.append("\n---------结束----------");
        
        return stringBuilder.toString();
    }
    
    
    /**
     * 打印多个变量值，使用逗号分隔，并添加分割线。
     *
     * @param values 要打印的变量值
     */
    public static void log(Object... values) {
        if (values.length == 0) {
            // 如果没有参数，直接返回
            return;
        }
        
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        String link = "-----------------------------------------\n" + ste.getFileName() + "/Line：" + ste.getLineNumber();
        
        int count = 1;
        StringBuilder logMessage = new StringBuilder(link);
        
        // 添加参数值
        for (Object value : values) {
            logMessage.append("\n----------").append("parameter").append(count++).append("------------------------\n").append(value).append("\n");
        }
        
        // 确保最后有一个分割线
        logMessage.append("\n------------------------------------------\n");
        
        // 使用info级别打印日志
        Log.i(TAG, logMessage.toString());
    }
    
    
    /**
     * 分享文本
     * <p>
     * 使用 fenXiang("哈哈哈", 123, true);
     */
    public void fenXiang(Object... contents) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, paramsToString(contents));
        shareIntent.setType("text/plain");
        Intent.createChooser(shareIntent, "分享到");
        startActivity(shareIntent);
    }
    
    
    /**
     * Toast
     *
     * @param showContents 提示内容
     */
    public void toast(Object... showContents) {
        if (showContents == null || showContents.length == 0) {
            return;
        }
        
        StringBuilder toastContent = new StringBuilder();
        for (int i = 0; i < showContents.length; i++) {
            if (showContents[i] != null) {
                toastContent.append(showContents[i].toString());
            }
            // 添加分隔符，除了最后一个元素
            if (i < showContents.length - 1) {
                toastContent.append("\n");
            }
        }
        
        // 使用 runOnUiThread 确保在主线程中更新 UI
        runOnUiThread(() -> {
            Toast.makeText(this, toastContent.toString(), Toast.LENGTH_SHORT).show();
        });
    }
    
    
    /**
     * 计算代码执行时间
     *
     * @param action 要执行的代码
     * @use runTime(() -> {})
     */
    public static void getRunTime(Runnable action) {
        // 开始时间
        long startTime = System.nanoTime();
        action.run();
        // 结束时间
        long endTime = System.nanoTime();
        log("执行时间：" + TimeUnit.NANOSECONDS.toMillis(endTime - startTime) + "毫秒");
    }
    
}