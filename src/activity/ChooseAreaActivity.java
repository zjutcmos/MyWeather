package activity;

import java.util.ArrayList;
import java.util.List;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import model.City;
import model.Country;
import model.Province;

import com.example.myweather.R;

import db.MyWeatherDB;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private TextView titleText;
	private ListView listView;
	private ProgressDialog progressDialog;
	private ArrayAdapter<String> adapter;
	private List<String> dataList = new ArrayList<String>();

	private MyWeatherDB myWeatherDB;

	/**
	 * 省级表
	 */
	private List<Province> provinceList;
	/**
	 * 市级表
	 */
	private List<City> cityList;

	/**
	 * 县级表
	 */
	private List<Country> countryList;

	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"from_weatherActivity", false);
		// 如果先前已经选择过了城市且不是从WeatherActivity跳转过来，则直接跳转到WeatherAcitivity,如果没有选择，则选择好县级后跳转顺便将coutryCode传过去
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("city_selected", false)
				&&!isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherAcitity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		myWeatherDB = MyWeatherDB.getInstance(this);// 通过单例模式确保只能获取一个MyWeatherDB
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 根据级别来判断调用查询哪个信息
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCountries();
				} else if (currentLevel == LEVEL_COUNTRY) {
					String countryCode = countryList.get(position)
							.getCountryCode();
					Log.d("选择的区域是:-->", countryCode);
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherAcitity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
				}
			}
		});
		// 加载省级数据
		queryProvinces();
	}

	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	private void queryProvinces() {
		provinceList = myWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询选中省内的所有城市，优先从数据库中查询，如果没有则从服务器上查询
	 */
	private void queryCities() {
		cityList = myWeatherDB.loadCity(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询所有的县，优先从数据库中查询，查询不到再去服务器上查询
	 */
	private void queryCountries() {
		countryList = myWeatherDB.loadCountry(selectedCity.getId());
		Log.d("countryList", countryList.size() + "=2===");
		if (countryList.size() > 0) {
			Log.d("activity", "-1-");
			dataList.clear();
			for (Country country : countryList) {
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		} else {
			Log.d("activity", "-2-");
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * 
	 * @param code
	 * @param type
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(myWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCityResponse(myWeatherDB, response,
							selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = Utility.handleCountryResponse(myWeatherDB,
							response, selectedCity.getId());
				}
				if (result) {
					// 通过runOnUiThread（）方法回到主线程处理逻辑
					// 此处很重要的一步，由于涉及到UI操作，因此必须要在主线程中调用，这里借助了runOnUiThread()方法实现了从子线程切换
					// 到主线程，它的原理其实也是基于异步消息处理机制的。
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("country".equals(type)) {
								queryCountries();
							}
						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread（）方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 1)
								.show();

					}
				});
			}
		});
	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTRY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherAcitity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}
