package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.City;
import model.Country;
import model.Province;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import db.MyWeatherDB;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(
			MyWeatherDB myWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");
			if (allProvince != null && allProvince.length > 0) {
				for (String string : allProvince) {
					String[] array = string.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到province表
					myWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理返回的市级数据
	 */
	public synchronized static boolean handleCityResponse(
			MyWeatherDB myWeatherDB, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCity = response.split(",");
			if (allCity != null && allCity.length > 0) {
				for (String string : allCity) {
					String[] array = string.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					myWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理返回的县级数据
	 */
	public synchronized static boolean handleCountryResponse(
			MyWeatherDB myWeatherDB, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCountry = response.split(",");
			if (allCountry != null && allCountry.length > 0) {
				for (String string : allCountry) {
					String[] array = string.split("\\|");
					Country country = new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					myWeatherDB.saveCountry(country);
				}
				Log.d("hand处理", "--1-");
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析服务器返回的json数据，并将其解析出的数据存储到本地
	 */
	public static void handleWeatherResponse(Context context, String response) {
		Log.d("要解析的json数据是-->", response);
		try {
			JSONObject jo = new JSONObject(response);
			JSONObject jsonObject2 = jo.getJSONObject("data");
			JSONArray weatherInfo = jsonObject2.getJSONArray("forecast");
			String city_name = jsonObject2.getString("city");
			// String publish_Time =
						// weatherInfo.getJSONObject(0).getString("ptime");// 发布时间
						String publish_Time = "9：00AM";
			//for (int i = 0; i < 4; i++) {
		
			String temp_high = weatherInfo.getJSONObject(0).getString("high");// 最高温度
			String temp_low = weatherInfo.getJSONObject(0).getString("low");// 最低温度
			String weather_description = weatherInfo.getJSONObject(0)
					.getString("type");// 天气描述
			saveWeatherInfo(context, city_name, weather_description, temp_high,
					temp_low, publish_Time);
			
				
		//	}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 * 
	 * @param context
	 * @param city_name
	 * @param weather_description
	 * @param temp_high
	 * @param temp_low
	 * @param publish_Time
	 */
	private static void saveWeatherInfo(Context context, String city_name,
			String weather_description, String temp_high, String temp_low,
			String publish_Time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", city_name);

		editor.putString("weather_description", weather_description);
		editor.putString("temp_high", temp_high);
		editor.putString("temp_low", temp_low);
		editor.putString("publish_Time", publish_Time);
		editor.putString("current_data", sdf.format(new Date()));
		editor.commit();

	}

}
