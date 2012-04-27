/*
 * ū ȭ���ε�, ���� �߿��Ѱ� ���� ����� Intent�� path�־ ����� �����ָ� �ȴٴ� ���̴�.
 * ���� ��ɵ��� ������ ����.(������ ��ɵ��� ���߿� �߰�)
 * �����̵�
 * ����
 * ����
 * ����
 * ���߿� Ȯ��/���, ȸ��, �ڸ��� ���� �߰��Ѵ�. �ð� �� ������ ������..
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