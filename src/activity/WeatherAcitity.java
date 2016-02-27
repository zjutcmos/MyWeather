package activity;

import service.AutoUpdateService;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

import com.example.myweather.R;

import android.app.Activity;
import android.app.SearchManager.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherAcitity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;// 显示城市名
	private TextView publishText;// 用于显示发布时间
	private TextView weatherDespText;// 用于天气情况的描述
	private TextView tempHighText, tempLowText;// 最高最低温度
	private TextView currentDateText;// 用于显示当前日期
	private Button switchCity;// 切换城市
	private Button refreshWeather;// 刷新天气

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		tempHighText = (TextView) findViewById(R.id.temp1);
		tempLowText = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		// closeStrictMode();
		String countryCode = getIntent().getStringExtra("country_code");// 手动选择城市时获得传过来的country_code
		if (!TextUtils.isEmpty(countryCode)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中......");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			// 没有县级代号时就直接显示本地天气
			showWeather();
		}
		//激活 AutoUpdateService 服务，实现后台定时更新的功能
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

	/*
	 * private void closeStrictMode() { StrictMode.setThreadPolicy(new
	 * StrictMode.ThreadPolicy.Builder() .detectAll().penaltyLog().build()); }
	 */

	/**
	 * 查询县级代号所对应的天气代号
	 * 
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	/**
	 * 查询天气代号所对应的天气
	 * 
	 * @param weatherCode
	 */
	public void queryWeatherInfo(String weatherCode) {
		String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="
				+ weatherCode;
		Log.d("weatherCode--->", weatherCode);

		queryFromServer(address, "weatherCode");
	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息 步骤：先获得县所对应的天气代号，再根据天气代号去解析天其信息
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
						// 从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							SharedPreferences.Editor sp = PreferenceManager
									.getDefaultSharedPreferences(
											WeatherAcitity.this).edit();
							sp.putString("weather_code", weatherCode);
							sp.commit();
							queryWeatherInfo(weatherCode);
							Log.d("countryCode对应的response--->", response);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
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
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(sharedPreferences.getString("city_name", ""));
		tempHighText.setText(sharedPreferences.getString("temp_high", ""));
		tempLowText.setText(sharedPreferences.getString("temp_low", ""));
		weatherDespText.setText(sharedPreferences.getString(
				"weather_description", ""));
		publishText.setText("今天 "
				+ sharedPreferences.getString("publish_Time", "") + "发布");
		currentDateText
				.setText(sharedPreferences.getString("current_data", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

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
			publishText.setText("正在刷新......");
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
