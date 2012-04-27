/*
 * 장기적으로 봤을 때 자체 앨범을 갖고 있는게 좋다. 필요하다면 갤러리에서도 볼 수 있으니 문제될 일은 없다.(카톡 프로필 등)
 * 
 * --- 일단 미리보기 페이지를 만드는데, 세로 스크롤로 한다. 찝찝했는데 생각해보면 가로가 더 비효율적이다. 조금씩 보이고 금방 사라지므로..
 * --- 큰화면도 구현하는데, 그건 그 파일에서 보자.
 * 해상도 줄여도 되지만 일단 bitmap 작게(?) 보여주는 방식 적용해서 파일 많아도 안 느리게 한다.
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
		// SD 카드가 없을 시 에러 처리한다.
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
			Toast.makeText(this, "SD 카드 없음", 1).show();
			finish();
			return;
		}
		
		mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/" + PICFOLDER;
		File file = new File(mRootPath);
		
		//없는 경우는 켜자마자(최초) 앨범 들갈때, 나중에 앨범 지우거나 지워졌을 때인데, 암튼 만들어준다.
		if (file.exists() == false) {
			if (file.mkdir() == false) {
				Toast.makeText(this, "폴더 생성 오류", 1).show();
				finish();
				return;
			}
		}
		
		int num = 0;
		int imgCount = file.listFiles().length;	// 파일 총 갯수 얻어오기
		map = new Bitmap[imgCount];
		path = new String[imgCount];
				
		if ( file.listFiles().length > 0 )
			for ( File f : file.listFiles() ) {
				path[num] = f.getName();				// 파일 이름 얻어오기
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
