package util;

/**
 * 回调服务返回的接口
 *
 */
public interface HttpCallbackListener {

	void onFinish(String response);

	void onError(Exception e);

}
