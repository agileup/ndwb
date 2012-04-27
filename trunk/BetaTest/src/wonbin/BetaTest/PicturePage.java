/*
 * 큰 화면인데, 먼저 중요한건 사진 찍고나서 Intent에 path넣어서 여기로 보내주면 된다는 것이다.
 * 세부 기능들은 다음과 같다.(세세한 기능들은 나중에 추가)
 * 슬라이드
 * 공유
 * 정보
 * 삭제
 * 나중에 확대/축소, 회전, 자르기 등을 추가한다. 시간 더 남으면 보정도..
 */

package wonbin.BetaTest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class PicturePage extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ImageView imageView = new ImageView(this);
		setContentView(imageView);

		Intent intent = getIntent();
		String path = intent.getStringExtra("path");
		try {
			Bitmap bm = BitmapFactory.decodeFile(path);
			imageView.setImageBitmap(bm);
		}
		catch (OutOfMemoryError e) {
			Toast.makeText(PicturePage.this,"out of mem",Toast.LENGTH_SHORT).show();
		}
	}
}