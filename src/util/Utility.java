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
import android.R.integer;
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
			String current_temp= jsonObject2.getString("wendu");//当前温度
			for (int i = 0; i < 4; i++) {
				String temp_high = weatherInfo.getJSONObject(i).getString(
						"high");// 最高温度
				String temp_low = weatherInfo.getJSONObject(i).getString("low");// 最低温度
				String weather_description = weatherInfo.getJSONObject(i)
						.getString("type");// 天气描述
				String fengxiang = weatherInfo.getJSONObject(i).getString(
						"fengxiang");// 风向
				String fengli = weatherInfo.getJSONObject(i)
						.getString("fengli");// 风力
				String date = weatherInfo.getJSONObject(i).getString("date");// 日期
				saveWeatherInfo(context, city_name, i, weather_description,
						current_temp,fengxiang, fengli, temp_high, temp_low, date);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 * 
	 * @param context
	 * @param city_name
	 * @param i
	 * @param weather_description
	 * @param current_temp
	 * @param fengxiang
	 * @param fengli
	 * @param temp_high
	 * @param temp_low
	 * @param date
	 */
	private static void saveWeatherInfo(Context context, String city_name,
			int i, String weather_description,String current_temp, String fengxiang, String fengli,
			String temp_high, String temp_low, String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();

		switch (i) {
		case 0:
			editor.putBoolean("city_selected", true);
			editor.putString("city_name", city_name);
			editor.putString("weather_description", weather_description);
			editor.putString("current_temp", current_temp);
			editor.putString("temp_high", temp_high);
			editor.putString("temp_low", temp_low);
			editor.putString("fengxiang", fengxiang);
			editor.putString("fengli", fengli);
			editor.putString("current_data", sdf.format(new Date()));
			break;
		case 1:
			editor.putString("weather_desp1", weather_description);
			editor.putString("high_temp1", temp_high);
			editor.putString("low_temp1", temp_low);
			editor.putString("date1", date);
			break;
		case 2:
			editor.putString("weather_desp2", weather_description);
			editor.putString("high_temp2", temp_high);
			editor.putString("low_temp2", temp_low);
			editor.putString("date2", date);
			break;
		case 3:
			editor.putString("weather_desp3", weather_description);
			editor.putString("high_temp3", temp_high);
			editor.putString("low_temp3", temp_low);
			editor.putString("date3", date);
			break;

		default:
			break;
		}

		editor.commit();

	}

}
