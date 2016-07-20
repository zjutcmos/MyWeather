package activity;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.zjutcmos.myweather.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {
	private EditText et_name, et_psd, et_psd2;
	private Button bt_register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		et_name = (EditText) findViewById(R.id.et_name);
		et_psd = (EditText) findViewById(R.id.et_psd);
		et_psd2 = (EditText) findViewById(R.id.et_psd2);
		bt_register = (Button) findViewById(R.id.bt_register);
		bt_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_register:
			if (checkRegister()) {

				BmobUser user = new BmobUser();
				user.setUsername(et_name.getText().toString().trim());
				user.setPassword(et_psd.getText().toString().trim());
				user.signUp(RegisterActivity.this, new SaveListener() {

					@Override
					public void onSuccess() {
						Toast.makeText(RegisterActivity.this, "注册成功",
								Toast.LENGTH_SHORT).show();
						finish();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						Toast.makeText(RegisterActivity.this, "注册失败",
								Toast.LENGTH_SHORT).show();

					}
				});
			}
			break;

		default:
			break;
		}
	}

	private boolean checkRegister() {
		boolean flag = true;
		String name = et_name.getText().toString().trim();
		String psd = et_psd.getText().toString().trim();
		String psd2 = et_psd2.getText().toString().trim();
		if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(psd)) {
			if (!psd.equals(psd2)) {
				Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", 1).show();
				flag = false;
			}
		}else {
			Toast.makeText(RegisterActivity.this, "用户名或密码不能为空", 1).show();
			flag = false;
		}
		return flag;
	}
}
