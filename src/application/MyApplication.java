package application;

import android.app.Application;
import android.content.Context;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
/**
 * 全局初始化类
 * @author Administrator
 *
 */
public class MyApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		Bmob.initialize(this, "d74e11e720ca3900b5e5bec49c7c0c19");//初始化第三方sdk的配置
		 // 使用推送服务时的初始化操作
	    BmobInstallation.getCurrentInstallation(this).save();
	    // 启动推送服务
	    BmobPush.startWork(this);
		super.onCreate();
	}
    /**
     * 获得全局context
     * @return
     */
	public static Context getContext() {
		return context;
	}
}
