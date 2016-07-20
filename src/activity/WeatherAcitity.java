package activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.zjutcmos.myweather.R;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.LogUtil;
import util.Utility;
import android.app.Activity;
import android.app.SearchManager.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 ��ʾ������Ϣ����
 *
 */
public class WeatherAcitity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;// ��ʾ������
	private TextView publishText;// ������ʾ����ʱ��
	private TextView weatherDespText;// �����������������
	private TextView tempHighText, tempLowText;// �������¶�
	private TextView currentDateText;// ������ʾ��ǰ����
	private TextView current_temp;// ������ʾ��ǰ�¶�
	private TextView fengxiang;// ������ʾ��ǰ����
	private TextView fengli;// ������ʾ��ǰ����
	private TextView weather_desp1, high_temp1, low_temp1, date1;// ���������
	private TextView weather_desp2, high_temp2, low_temp2, date2;// ���������
	private TextView weather_desp3, high_temp3, low_temp3, date3;// ��������
	private Button switchCity;// �л�����
	private Button refreshWeather;// ˢ������
	private RelativeLayout layout_bg;// ����������������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		initView();// ��ʼ���ؼ�

		// closeStrictMode();
		String countryCode = getIntent().getStringExtra("country_code");// �ֶ�ѡ�����ʱ��ô�������country_code
		if (!TextUtils.isEmpty(countryCode)) {
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����......");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		// ���� AutoUpdateService ����ʵ�ֺ�̨��ʱ���µĹ���
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	private void initView() {
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		layout_bg = (RelativeLayout) findViewById(R.id.weather_bg);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		tempHighText = (TextView) findViewById(R.id.high_temp);
		tempLowText = (TextView) findViewById(R.id.low_temp);
		fengxiang = (TextView) findViewById(R.id.fengxiang);
		fengli = (TextView) findViewById(R.id.fengli);
		currentDateText = (TextView) findViewById(R.id.current_date);
		current_temp = (TextView) findViewById(R.id.current_temp);
		weather_desp1 = (TextView) findViewById(R.id.weather_desp1);
		high_temp1 = (TextView) findViewById(R.id.high_temp1);
		low_temp1 = (TextView) findViewById(R.id.low_temp1);
		date1 = (TextView) findViewById(R.id.date1);
		weather_desp2 = (TextView) findViewById(R.id.weather_desp2);
		high_temp2 = (TextView) findViewById(R.id.high_temp2);
		low_temp2 = (TextView) findViewById(R.id.low_temp2);
		date2 = (TextView) findViewById(R.id.date2);
		weather_desp3 = (TextView) findViewById(R.id.weather_desp3);
		high_temp3 = (TextView) findViewById(R.id.high_temp3);
		low_temp3 = (TextView) findViewById(R.id.low_temp3);
		date3 = (TextView) findViewById(R.id.date3);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);

	}

	/*
	 * private void closeStrictMode() { StrictMode.setThreadPolicy(new
	 * StrictMode.ThreadPolicy.Builder() .detectAll().penaltyLog().build()); }
	 */

	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * 
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	/**
	 * ��ѯ������������Ӧ������
	 * 
	 * @param weatherCode
	 */
	public void queryWeatherInfo(String weatherCode) {
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="
				+ weatherCode;
		LogUtil.d("weatherCode--->", weatherCode);
        Log.d("weatherCode--->", weatherCode);
		queryFromServer(address, "weatherCode");
	}

	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ ���裺�Ȼ��������Ӧ���������ţ��ٸ�����������ȥ����������Ϣ
	 * 
	 * @param address
	 * @param string
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				if ("countryCode".equals(type)) {

					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							SharedPreferences.Editor sp = PreferenceManager
									.getDefaultSharedPreferences(
											WeatherAcitity.this).edit();
							sp.putString("weather_code", weatherCode);
							sp.commit();
							queryWeatherInfo(weatherCode);
							LogUtil.d("countryCode��Ӧ��response--->", response);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// ������������ص�������Ϣ
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Utility.handleWeatherResponse(WeatherAcitity.this,
									response);
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weather_description = sharedPreferences.getString(
				"weather_description", "");
		changeBackground(weather_description);
		cityNameText.setText(sharedPreferences.getString("city_name", ""));
		current_temp.setText(sharedPreferences.getString("current_temp", ""));
		tempHighText.setText(sharedPreferences.getString("temp_high", ""));
		tempLowText.setText(sharedPreferences.getString("temp_low", ""));
		fengli.setText(sharedPreferences.getString("fengli", ""));
		fengxiang.setText(sharedPreferences.getString("fengxiang", ""));
		weatherDespText.setText(weather_description);
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.CHINA);
		String publish_Time = sdf2.format(new Date());
		publishText.setText("���� " + publish_Time + "����");
		currentDateText
				.setText(sharedPreferences.getString("current_data", ""));
		// ��3�������
		weather_desp1.setText(sharedPreferences.getString("weather_desp1", ""));
		high_temp1.setText(sharedPreferences.getString("high_temp1", ""));
		low_temp1.setText(sharedPreferences.getString("low_temp1", ""));
		date1.setText(sharedPreferences.getString("date1", ""));
		weather_desp2.setText(sharedPreferences.getString("weather_desp2", ""));
		high_temp2.setText(sharedPreferences.getString("high_temp2", ""));
		low_temp2.setText(sharedPreferences.getString("low_temp2", ""));
		date2.setText(sharedPreferences.getString("date2", ""));
		weather_desp3.setText(sharedPreferences.getString("weather_desp3", ""));
		high_temp3.setText(sharedPreferences.getString("high_temp3", ""));
		low_temp3.setText(sharedPreferences.getString("low_temp3", ""));
		date3.setText(sharedPreferences.getString("date3", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

	}

	/**
	 * ����������Ϣ�任����ͼƬ
	 * 
	 * @param weather_description
	 */
	private void changeBackground(String weather_description) {
		if (!TextUtils.isEmpty(weather_description)) {
			Log.e("������Ϣ---->", weather_description);
			switch (weather_description) {
			case "��":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.qing));
				break;
			case "��":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.yin));
				break;
			case "С��":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.yu));
				break;
			case "Сѩ":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.xue));
				break;
			case "����":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.duoyun));
				break;
			default:
				layout_bg.setBackgroundColor(0xff27A5F9);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weatherActivity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("����ˢ��......");
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = sharedPreferences
					.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;

		default:
			break;
		}
	}

}
