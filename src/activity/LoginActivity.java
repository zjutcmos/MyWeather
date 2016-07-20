package activity;

import com.zjutcmos.myweather.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private EditText et_name, et_psd;
	private Button bt_register, bt_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		et_name = (EditText) findViewById(R.id.et_name);
		et_psd = (EditText) findViewById(R.id.et_psd);
		bt_register = (Button) findViewById(R.id.bt_register);
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(this);
		bt_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.bt_register:
			Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
			startActivity(i);
			/*// 使用BmobSDK提供的注册功能
			 * 
			BmobUser user = new BmobUser();
			user.setUsername(name);
			user.setPassword(psd);
			user.signUp(LoginActivity.this, new SaveListener() {

				@Override
				public void onSuccess() {
					Toast.makeText(LoginActivity.this, "注册成功",
							Toast.LENGTH_SHORT).show();
                     et_psd.setText("");
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					Toast.makeText(LoginActivity.this, "注册失败",
							Toast.LENGTH_SHORT).show();

				}
			});*/
			
			break;
		case R.id.bt_login:
			String name = et_name.getText().toString();
			String psd = et_psd.getText().toString();
			if (TextUtils.isEmpty(name) || TextUtils.isEmpty(psd)) {
				Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			BmobUser user2 = new BmobUser();
			user2.setUsername(name);
			user2.setPassword(psd);
			//神奇之处在于自带验证
			user2.login(this, new SaveListener() {
				@Override
				public void onSuccess() {
					Toast.makeText(LoginActivity.this, "登录成功",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, ChooseAreaActivity.class);
					startActivity(intent);
					finish();
					
				}

				@Override
				public void onFailure(int i, String s) {
					Toast.makeText(LoginActivity.this, "登录失败,用户名或密码错误",
							Toast.LENGTH_SHORT).show();
				}
			});
			break;

		default:
			break;
		}
	}
}
