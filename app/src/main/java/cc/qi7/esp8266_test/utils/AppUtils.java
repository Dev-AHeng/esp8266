package cc.qi7.esp8266_test.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @author Dev_Heng
 * @date 2024年9月18日00:59:21
 */
public class AppUtils {
    
    /**
     * 判断指定包名的应用是否已安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 如果已安装，则返回true；否则返回false
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}