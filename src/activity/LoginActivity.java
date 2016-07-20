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
		// TODO �Զ����ɵķ������
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
			/*// ʹ��BmobSDK�ṩ��ע�Ṧ��
			 * 
			BmobUser user = new BmobUser();
			user.setUsername(name);
			user.setPassword(psd);
			user.signUp(LoginActivity.this, new SaveListener() {

				@Override
				public void onSuccess() {
					Toast.makeText(LoginActivity.this, "ע��ɹ�",
							Toast.LENGTH_SHORT).show();
                     et_psd.setText("");
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					Toast.makeText(LoginActivity.this, "ע��ʧ��",
							Toast.LENGTH_SHORT).show();

				}
			});*/
			
			break;
		case R.id.bt_login:
			String name = et_name.getText().toString();
			String psd = et_psd.getText().toString();
			if (TextUtils.isEmpty(name) || TextUtils.isEmpty(psd)) {
				Toast.makeText(this, "�û��������벻��Ϊ��", Toast.LENGTH_SHORT).show();
				return;
			}
			BmobUser user2 = new BmobUser();
			user2.setUsername(name);
			user2.setPassword(psd);
			//����֮�������Դ���֤
			user2.login(this, new SaveListener() {
				@Override
				public void onSuccess() {
					Toast.makeText(LoginActivity.this, "��¼�ɹ�",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, ChooseAreaActivity.class);
					startActivity(intent);
					finish();
					
				}

				@Override
				public void onFailure(int i, String s) {
					Toast.makeText(LoginActivity.this, "��¼ʧ��,�û������������",
							Toast.LENGTH_SHORT).show();
				}
			});
			break;

		default:
			break;
		}
	}
}
