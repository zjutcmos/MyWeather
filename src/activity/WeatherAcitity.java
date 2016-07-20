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
 显示天气信息界面
 *
 */
public class WeatherAcitity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;// 显示城市名
	private TextView publishText;// 用于显示发布时间
	private TextView weatherDespText;// 用于天气情况的描述
	private TextView tempHighText, tempLowText;// 最高最低温度
	private TextView currentDateText;// 用于显示当前日期
	private TextView current_temp;// 用于显示当前温度
	private TextView fengxiang;// 用于显示当前风向
	private TextView fengli;// 用于显示当前风力
	private TextView weather_desp1, high_temp1, low_temp1, date1;// 明天的天气
	private TextView weather_desp2, high_temp2, low_temp2, date2;// 后天的天气
	private TextView weather_desp3, high_temp3, low_temp3, date3;// 外后的天气
	private Button switchCity;// 切换城市
	private Button refreshWeather;// 刷新天气
	private RelativeLayout layout_bg;// 整个天气背景布局

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		initView();// 初始化控件

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
		// 激活 AutoUpdateService 服务，实现后台定时更新的功能
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
		LogUtil.d("weatherCode--->", weatherCode);
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
							LogUtil.d("countryCode对应的response--->", response);
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
		publishText.setText("今天 " + publish_Time + "发布");
		currentDateText
				.setText(sharedPreferences.getString("current_data", ""));
		// 后3天的天气
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
	 * 根据天气信息变换背景图片
	 * 
	 * @param weather_description
	 */
	private void changeBackground(String weather_description) {
		if (!TextUtils.isEmpty(weather_description)) {
			Log.e("天气信息---->", weather_description);
			switch (weather_description) {
			case "晴":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.qing));
				break;
			case "阴":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.yin));
				break;
			case "小雨":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.yu));
				break;
			case "小雪":
				layout_bg.setBackground(getResources().getDrawable(
						R.drawable.xue));
				break;
			case "多云":
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
