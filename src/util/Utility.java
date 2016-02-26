package util;

import model.City;
import model.Country;
import model.Province;
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
			MyWeatherDB myWeatherDB, String response,int provinceId) {
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
	public synchronized static boolean handleCountryResponse(MyWeatherDB myWeatherDB,
			String response,int cityId) {
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

}
