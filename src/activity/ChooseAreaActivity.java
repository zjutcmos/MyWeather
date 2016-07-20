package activity;

import java.util.ArrayList;
import java.util.List;

import com.zjutcmos.myweather.R;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.LogUtil;
import util.Utility;
import model.City;
import model.Country;
import model.Province;
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

/**
 * ѡ����н���
 * 
 */
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
	 * ʡ����
	 */
	private List<Province> provinceList;
	/**
	 * �м���
	 */
	private List<City> cityList;

	/**
	 * �ؼ���
	 */
	private List<Country> countryList;

	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"from_weatherActivity", false);
		// �����ǰ�Ѿ�ѡ����˳����Ҳ��Ǵ�WeatherActivity��ת��������ֱ����ת��WeatherAcitivity,���û��ѡ����ѡ����ؼ�����ת˳�㽫coutryCode����ȥ
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
		myWeatherDB = MyWeatherDB.getInstance(this);// ͨ������ģʽȷ��ֻ�ܻ�ȡһ��MyWeatherDB�������������ݿ�
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ���ݼ������жϵ��ò�ѯ�ĸ���Ϣ
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCountries();
				} else if (currentLevel == LEVEL_COUNTRY) {
					String countryCode = countryList.get(position)
							.getCountryCode();
					Log.d("ѡ���������:-->", countryCode);
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherAcitity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
				}
			}
		});
		// ����ʡ������
		queryProvinces();
	}

	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryProvinces() {
		provinceList = myWeatherDB.loadProvince();
		if (provinceList.size() > 0) {
			//clear()��ǰ�ı������������õ����list�ı�����ָ��ͬһ���յ�list
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * ��ѯѡ��ʡ�ڵ����г��У����ȴ����ݿ��в�ѯ�����û����ӷ������ϲ�ѯ
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
	 * ��ѯ���е��أ����ȴ����ݿ��в�ѯ����ѯ������ȥ�������ϲ�ѯ
	 */
	private void queryCountries() {
		countryList = myWeatherDB.loadCountry(selectedCity.getId());
		Log.d("countryList", countryList.size() + "=2===");
		if (countryList.size() > 0) {
			LogUtil.d("activity", "-1-");
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
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
					// ͨ��runOnUiThread���������ص����̴߳����߼�
					// �˴�����Ҫ��һ���������漰��UI��������˱���Ҫ�����߳��е��ã����������runOnUiThread()����ʵ���˴����߳��л�
					// �����̣߳�����ԭ����ʵҲ�ǻ����첽��Ϣ������Ƶġ�
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
					//��ȻҲ����ͨ��Handler�������ص�����
					/*�Ȱѽ������ȥ�������̵߳�handleMessage(Message msg)����
					 *Message msg=new Message();
					 *msg.what=0x01;SHOW_RESPONSE
					 *msg.obj=result;
					 *handler.sendMessage(msg);
					 * 
					 */
				}

			}

			@Override
			public void onError(Exception e) {
				// ͨ��runOnUiThread���������ص����̴߳����߼�
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", 1)
								.show();

					}
				});
			}
		});
	}

	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTRY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromWeatherActivity) {//����ǵ����������������ó��а�ť���ٷ���
				Intent intent = new Intent(this, WeatherAcitity.class);
				startActivity(intent);
			}
			finish();
		}
	}

}
