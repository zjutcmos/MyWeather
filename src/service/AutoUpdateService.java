package service;

import receiver.AutoUpdateReceiver;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	// ÿ�η���������ʱ�����
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour =8 * 60 * 60 * 1000;// ����8Сʱ�ĺ����� Ϊ�������������͵�����8Сʱ�Զ�����һ��
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent intent2 = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent2, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * ����������Ϣ
	 */
	private void updateWeather() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = preferences.getString("weather_code", "");
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="
				+ weatherCode;

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception e) {
				// TODO �Զ����ɵķ������

			}
		});
	}

}
