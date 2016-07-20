package receiver;

import org.json.JSONException;
import org.json.JSONObject;

import activity.WeatherAcitity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.bmob.push.PushConstants;

import com.zjutcmos.myweather.R;

public class PushReceiver extends BroadcastReceiver{
   static  int i=0;
	@Override
	public void onReceive(Context context, Intent intent) {
		i++;
		// TODO Auto-generated method stub
        if(intent.getAction().equals("cn.bmob.push.action.MESSAGE")){
			String msg = intent.getStringExtra("msg");
			String msg_content = "";
			try {
				JSONObject mJsonObject=new JSONObject(msg);
				msg_content = mJsonObject.getString("alert");
			} catch (JSONException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			
            Log.e("bmob", "�ͻ����յ���������---->��"+intent.getStringExtra("msg"));
            
         // ��Android����֪ͨ����������Ҫ��ϵͳ������֪ͨ������NotificationManager������һ��ϵͳService��  
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
         // ����һ��PendingIntent����Intent���ƣ���ͬ�������ڲ������ϵ��ã���Ҫ������״̬��������activity�����Բ��õ���PendingIntent,�����Notification��ת�������ĸ�Activity  
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,  
                    new Intent(context, WeatherAcitity.class), 0);  
            // ���������Android 2.x�汾�ǵĴ���ʽ  
         // API11֮���֧��  
            Notification notify = new Notification.Builder(context)  
                    .setSmallIcon(R.drawable.ic_launcher) // ����״̬���е�СͼƬ���ߴ�һ�㽨����24��24�����ͼƬͬ��Ҳ��������״̬��������ʾ�������������Ҫ���������ͼƬ������ʹ��setLargeIcon(Bitmap  
                                                        // icon)  
                    .setTicker("�����µ�Ӧ����Ϣ����ע����գ�")// ������status  
                                                                // bar����ʾ����ʾ����  
                    .setContentTitle("��������")// ����������status  
                                                            // bar��Activity���������е�NotififyMessage��TextView����ʾ�ı���  
                    .setContentText(msg_content)// TextView����ʾ����ϸ����  
                    .setContentIntent(pendingIntent) // ����PendingIntent  
                    .setNumber(i) // ��TextView���ҷ���ʾ�����֣��ɷŴ�ͼƬ���������Ҳࡣ���numberͬʱҲ��һ�����кŵ����ң��������������֪ͨ��ͬһID��������ָ����ʾ��һ����  
                    .getNotification(); // ��Ҫע��build()����API level  
            notify.flags |= Notification.FLAG_AUTO_CANCEL;  
            // ͨ��֪ͨ������������֪ͨ�����id��ͬ����ÿclick����status��������һ����ʾ  
            manager.notify(i, notify);  
            Log.e("i��ֵ----��", i+"");
        }
	}

}
