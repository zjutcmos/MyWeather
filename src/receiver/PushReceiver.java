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
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
            Log.e("bmob", "客户端收到推送内容---->："+intent.getStringExtra("msg"));
            
         // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。  
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
         // 创建一个PendingIntent，和Intent类似，不同的是由于不是马上调用，需要在下拉状态条出发的activity，所以采用的是PendingIntent,即点击Notification跳转启动到哪个Activity  
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,  
                    new Intent(context, WeatherAcitity.class), 0);  
            // 下面需兼容Android 2.x版本是的处理方式  
         // API11之后才支持  
            Notification notify = new Notification.Builder(context)  
                    .setSmallIcon(R.drawable.ic_launcher) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap  
                                                        // icon)  
                    .setTicker("您有新的应用消息，请注意查收！")// 设置在status  
                                                                // bar上显示的提示文字  
                    .setContentTitle("菠萝天气")// 设置在下拉status  
                                                            // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题  
                    .setContentText(msg_content)// TextView中显示的详细内容  
                    .setContentIntent(pendingIntent) // 关联PendingIntent  
                    .setNumber(i) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。  
                    .getNotification(); // 需要注意build()是在API level  
            notify.flags |= Notification.FLAG_AUTO_CANCEL;  
            // 通过通知管理器来发起通知。如果id不同，则每click，在status那里增加一个提示  
            manager.notify(i, notify);  
            Log.e("i的值----》", i+"");
        }
	}

}
