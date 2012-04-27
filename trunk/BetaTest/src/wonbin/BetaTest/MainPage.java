/*
 * --- 일단 layout을 이미지버튼으로만 간단하게 구성한다.
 * --- 버튼은 사진, 앨범, 설정, 도움말 이렇게 4개가 필요하다.
 * --- 뒷 배경도 필요하다.
 * 해상도는 480*800(WVGA)기준으로 레이아웃 잘 짠다. 디자인은 아저씨도 있고.. 블랙&화이트로 간다.(만들기도 좋다.)
 * 참고로, 기본 160dpi 320*480 size이므로, 비율은 480*800이더라도 240dpi임을 고려해서 2/3을 곱한 dp를 반영해야 160dpi기준에 맞춰지는 것이 된다.(이미지 비율은 새로 디자인 할 때 다시 맞춘다.)
 * pref로 첫 실행시 도움말부터 뜨게 한다.(테스트때는 항상 도움말부터 뜨게 한다.)
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
		
		// 이 사이에서 loading을 한다.
		Intent intent;
        intent=new Intent(MainPage.this, LoadingPage.class);
		startActivity(intent);
		
		//먼저 view 등록
		setContentView(R.layout.mainpage);
		
		//button listener 달기
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