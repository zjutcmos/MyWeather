package application;

import android.app.Application;
import android.content.Context;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
/**
 * ȫ�ֳ�ʼ����
 * @author Administrator
 *
 */
public class MyApplication extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		Bmob.initialize(this, "d74e11e720ca3900b5e5bec49c7c0c19");//��ʼ��������sdk������
		 // ʹ�����ͷ���ʱ�ĳ�ʼ������
	    BmobInstallation.getCurrentInstallation(this).save();
	    // �������ͷ���
	    BmobPush.startWork(this);
		super.onCreate();
	}
    /**
     * ���ȫ��context
     * @return
     */
	public static Context getContext() {
		return context;
	}
}
