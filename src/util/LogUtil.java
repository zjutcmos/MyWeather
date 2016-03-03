package util;

import android.util.Log;
/**
 * 定制日志调试工具
 *  
 * @author Administrator
 *
 */
public class LogUtil {
	private static final int VERBOSE = 1;
	private static final int DEBUG = 2;
	private static final int INFO = 3;
	private static final int WARN = 4;
	private static final int ERROR = 5;
	private static final int NOTHING = 6;
	private static final int LEVEL = VERBOSE;//修改这里的值就可以自由地控制日志的打印行为了
	//当LEVEL = VERBOSE时，把所有的日志都打印出来，当LEVEL = NOTHING时就屏蔽了所有的日志

	public static void v(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LEVEL <= VERBOSE) {
			Log.e(tag, msg);
		}
	}

}
