package receiver;

import service.AutoUpdateService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent2 = new Intent(context, AutoUpdateService.class);
		context.startService(intent2);
	}

}
