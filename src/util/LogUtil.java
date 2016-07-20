package util;

import android.util.Log;
/**
 * ������־���Թ���
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
	private static final int LEVEL = DEBUG;//�޸������ֵ�Ϳ������ɵؿ�����־�Ĵ�ӡ��Ϊ��
	//��LEVEL = VERBOSEʱ�������е���־����ӡ��������LEVEL = NOTHINGʱ�����������е���־

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
