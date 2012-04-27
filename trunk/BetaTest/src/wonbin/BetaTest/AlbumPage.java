/*
 * ��������� ���� �� ��ü �ٹ��� ���� �ִ°� ����. �ʿ��ϴٸ� ������������ �� �� ������ ������ ���� ����.(ī�� ������ ��)
 * 
 * --- �ϴ� �̸����� �������� ����µ�, ���� ��ũ�ѷ� �Ѵ�. �����ߴµ� �����غ��� ���ΰ� �� ��ȿ�����̴�. ���ݾ� ���̰� �ݹ� ������Ƿ�..
 * --- ūȭ�鵵 �����ϴµ�, �װ� �� ���Ͽ��� ����.
 * �ػ� �ٿ��� ������ �ϴ� bitmap �۰�(?) �����ִ� ��� �����ؼ� ���� ���Ƶ� �� ������ �Ѵ�.
 */

package wonbin.BetaTest;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class AlbumPage extends Activity {
	GridView mGrid;
	Cursor mCursor;
	
	String mRootPath;
	static final String PICFOLDER = "SHCamera";
	Bitmap[] map;
	String[] path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albumpage);
		
		mGrid = (GridView) findViewById(R.id.imagegrid);
/*
		ContentResolver cr = getContentResolver();
		mCursor = cr.query(Images.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null, null);// "bucket_display_name=SHCamera"
*/
		// SD ī�尡 ���� �� ���� ó���Ѵ�.
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
			Toast.makeText(this, "SD ī�� ����", 1).show();
			finish();
			return;
		}
		
		mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/" + PICFOLDER;
		File file = new File(mRootPath);
		
		//���� ���� ���ڸ���(����) �ٹ� �鰥��, ���߿� �ٹ� ����ų� �������� ���ε�, ��ư ������ش�.
		if (file.exists() == false) {
			if (file.mkdir() == false) {
				Toast.makeText(this, "���� ���� ����", 1).show();
				finish();
				return;
			}
		}
		
		int num = 0;
		int imgCount = file.listFiles().length;	// ���� �� ���� ������
		map = new Bitmap[imgCount];
		path = new String[imgCount];
				
		if ( file.listFiles().length > 0 )
			for ( File f : file.listFiles() ) {
				path[num] = f.getName();				// ���� �̸� ������
				map[num] = BitmapFactory.decodeFile(mRootPath+"/"+path[num]);
				num++;
			}
	
		ImageAdapter Adapter = new ImageAdapter(this);
		mGrid.setAdapter(Adapter);
			
		mGrid.setOnItemClickListener(mItemClickListener);
	}
	
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//mCursor.moveToPosition(position);
			//String path = mCursor.getString(mCursor.getColumnIndex(Images.ImageColumns.DATA));
			Intent intent = new Intent(AlbumPage.this, PicturePage.class);
			intent.putExtra("path", mRootPath+"/"+path[position]);
			startActivity(intent);
		}
	};

	class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			//return mCursor.getCount();
			return map.length;
		}

		@Override
		public Object getItem(int position) {
			//return map[position];
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setImageBitmap(map[position]);
				imageView.setAdjustViewBounds(true);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
			else imageView = (ImageView) convertView;

			return imageView;
		}
	}
}
