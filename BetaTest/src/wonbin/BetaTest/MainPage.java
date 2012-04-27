/*
 * --- �ϴ� layout�� �̹�����ư���θ� �����ϰ� �����Ѵ�.
 * --- ��ư�� ����, �ٹ�, ����, ���� �̷��� 4���� �ʿ��ϴ�.
 * --- �� ��浵 �ʿ��ϴ�.
 * �ػ󵵴� 480*800(WVGA)�������� ���̾ƿ� �� §��. �������� �������� �ְ�.. ��&ȭ��Ʈ�� ����.(����⵵ ����.)
 * �����, �⺻ 160dpi 320*480 size�̹Ƿ�, ������ 480*800�̴��� 240dpi���� ����ؼ� 2/3�� ���� dp�� �ݿ��ؾ� 160dpi���ؿ� �������� ���� �ȴ�.(�̹��� ������ ���� ������ �� �� �ٽ� �����.)
 * pref�� ù ����� ���򸻺��� �߰� �Ѵ�.(�׽�Ʈ���� �׻� ���򸻺��� �߰� �Ѵ�.)
 */

package wonbin.BetaTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainPage extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// �� ���̿��� loading�� �Ѵ�.
		Intent intent;
        intent=new Intent(MainPage.this, LoadingPage.class);
		startActivity(intent);
		
		//���� view ���
		setContentView(R.layout.mainpage);
		
		//button listener �ޱ�
		findViewById(R.id.mainpage_btn_camera).setOnClickListener(listenerBtn);
		findViewById(R.id.mainpage_btn_album).setOnClickListener(listenerBtn);
		findViewById(R.id.mainpage_btn_option).setOnClickListener(listenerBtn);
		findViewById(R.id.mainpage_btn_help).setOnClickListener(listenerBtn);
    }
    
    View.OnClickListener listenerBtn=new View.OnClickListener()
    {
		@Override
		public void onClick(View v)
		{
			Intent intent;
			
			switch(v.getId())
			{
			case R.id.mainpage_btn_camera:
		        intent=new Intent(MainPage.this, CameraPage.class);
				startActivity(intent);
				break;
			case R.id.mainpage_btn_album:
		        intent=new Intent(MainPage.this, AlbumPage.class);
				startActivity(intent);
				break;
			case R.id.mainpage_btn_option:
		        intent=new Intent(MainPage.this, OptionPage.class);
				startActivity(intent);
				break;
			case R.id.mainpage_btn_help:
		        intent=new Intent(MainPage.this, HelpPage.class);
				startActivity(intent);
				break;
			}
		}
	};
}