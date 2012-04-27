/*
 * 일단 대충 틀을 가진 프로젝트를 갖고왔는데 여기서 이제 손을 좀 봐야된다.
 * 
 * --- 먼저, min 2.2로 맞춘다. 11년 5월쯤 나왔으므로 그 이상으로 하긴 좀 그렇고 그럴 필요도 없다.
 *     전면 카메라 쓰려면 2.3이상이어야 하는데, 일단 없어도 사운드 있으므로 놔둔다.
 * --- 접사 기능 없앤다.
 * --- autofocus 실패해도 찍게해준다.
 * --- 앨범도 고친다. - Activity 하나 만들고 촬영 후에도 Intent로 보여주도록 한다.
 * 레이아웃 고친다.
 * portrait로 바꾼다.
 * 찍은 이미지도 방향 고친다.
 * 촬영소리 넣는다.(해제 가능하게 해둔다?)(넥원은 소리난다...)
 * --- 터치, 롱터치 추가
 * 방향 추천 레이아웃 넣기
 * --- 전면카메라 default로
 * --- 서버로 전송할 이미지 추출 - 중요하며, 원본이 필요한지, 눈깜박임이나 인식 부정확으로 인해 완벽히 인식이 안될 경우를 대비해 여러장 보내는 것 등을 고려해봐야 한다.
 * 							일단 모두 주석처리해둔다.(서버 안쓰고 내장 방식 사용 예정)
 * 앨범 썸네일 관련해서도 잘 알아봐야 한다.
 * --- 시간나면 pref왜있는지 살펴본다. => 해상도 저장용
 */

package wonbin.BetaTest;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Camera;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class CameraPage extends Activity {
	String mRootPath;
	FrameLayout mTakePicture;
	ImageView mReview;
	SHCameraSurface mSurface;
	int mPicWidth, mPicHeight;
	int mSelect;
	String mLastPicture = "";
	static final String PICFOLDER = "SHCamera";
	static final int TAKEDELAY = 300;
	Context mMainContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camerapage);
		
		mMainContext = this;
		
		// SD 카드가 없을 시 에러 처리한다.
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
			Toast.makeText(this, "SD 카드 없음", 1).show();
			finish();
			return;
		}
		
		mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/" + PICFOLDER;
		File fRoot = new File(mRootPath);
		if (fRoot.exists() == false) {
			if (fRoot.mkdir() == false) {
				Toast.makeText(this, "폴더 생성 오류", 1).show();
				finish();
				return;
			}
		}
		
		// 프레퍼런스에서 크기 읽어 옴
		SharedPreferences pref = getSharedPreferences("SHCamera",0);
		mPicWidth = pref.getInt("PicWidth", -1);
		mPicHeight = pref.getInt("PicHeight", -1);
		
		// 버튼들의 클릭 리스너 지정
		mTakePicture = (FrameLayout)findViewById(R.id.takepicture);
		mSurface = (SHCameraSurface)findViewById(R.id.preview);
		
		//touch 설정
		mSurface.setOnClickListener(mClickListener);
		mSurface.setOnLongClickListener(mLongClickListener);
	}
	
	View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//다음은 이동량을 계산한다. ---> processor에서 구한다.
			//다음은 이동을 추적할 센서를 준비한다. ---> sensor부분에서 처리
			//다음은 이동방향을 표시한다. 그러면서 이동을 계속 체크한다.
			Toast.makeText(CameraPage.this, "터치", 1).show();
		}
	};
	
	View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			mSurface.mCamera.autoFocus(mAutoFocus);
			//Toast.makeText(CameraTestActivity.this, "롱터치", 1).show();
			return true;
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	// 포커싱 성공하면 촬영
	AutoFocusCallback mAutoFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera)
		{
			mTakePicture.postDelayed(new Runnable(){
				@Override
				public void run()
				{
					mSurface.mCamera.takePicture(null, null, mPicture); // ------ 나중에 다시 보기 (이미지 보내야 할수도 있음)
				}
			}, TAKEDELAY);// delay는 아마 이 함수 return 시간인 것 같으므로, success여부에 관계없이 사용하도록 한다.
			// 나중에 focusing과정 visualization(초록, 빨강 등으로)도 하도록 한다.
		}
	};

	// 사진 저장. 날짜와 시간으로 파일명 결정하고 저장후 미디어 스캔 실행
	// --- 여기서도 90도 돌려서 저장해줘야 한다.
	PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Calendar calendar = Calendar.getInstance();
			String FileName = String.format("SH%02d%02d%02d-%02d%02d%02d.jpg", 
					calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH)+1, 
					calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 
					calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
			String path = mRootPath + "/" + FileName;

			File file = new File(path);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				Toast.makeText(mMainContext, "파일 저장 중 에러 발생 : " + e.getMessage(), 0).show();
				return;
			}

			// 스캐닝 요청
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.parse("file://" + path);
			intent.setData(uri);
			sendBroadcast(intent);

			mLastPicture = path;
			
			//여기서 하는게 맞는진 몰겠지만 앨범 불러준다.(postview에서도 되겠지만 저장 후가 더 낫고, back하면 밑에 startPreview되도록.)
			//(현재 project가 떨어져있어서 임시로 picturepage도 복사해놓았다.)
			intent = new Intent(CameraPage.this, PicturePage.class);
			intent.putExtra("path", mLastPicture);
			startActivity(intent);
			
			//혹시 사진찍고 돌아왔더니 90도 돌아가있다면 여기도 setDisplayOrientation(90); 해준다.
			mSurface.mCamera.startPreview();
		}
	};
	
	//가능 해상도 종류를 구하고 메뉴에 일단 첫번째 걸로 넣어놓는다.(젤 높은 해상도)
	Button.OnClickListener mSizeClick = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			Parameters params = mSurface.mCamera.getParameters();
	        final List<Size> arSize = params.getSupportedPictureSizes();
	        String[] arName = new String[arSize.size()];
	        for (int i = 0; i < arSize.size(); i++) {
	        	Size s = arSize.get(i);
	        	arName[i] = String.format("%d * %d", s.width, s.height);
	        	if (mPicWidth == s.width && mPicHeight == s.height) {
	        		mSelect = i;
	        	}
	        }
	        
			new AlertDialog.Builder(mMainContext)
			.setTitle("사진 해상도 선택")
			.setSingleChoiceItems(arName, mSelect, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelect = which;
				}
			})
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					Parameters params = mSurface.mCamera.getParameters();
					int width = arSize.get(mSelect).width;
					int height = arSize.get(mSelect).height;
					params.setPictureSize(width, height);
					mSurface.mCamera.setParameters(params);
					
					SharedPreferences pref = getSharedPreferences("SHCamera",0);
					SharedPreferences.Editor edit = pref.edit();
					edit.putInt("PicWidth", width);
					edit.putInt("PicHeight", height);
					edit.commit();
				}
			})
			.setNegativeButton("취소", null)
			.show();
		}
	};
}

// 미리보기 표면 클래스
class SHCameraSurface extends SurfaceView implements SurfaceHolder.Callback {
	SurfaceHolder mHolder;
	Context mContext;
	Camera mCamera;

	public SHCameraSurface(Context context) {
		super(context);
		init(context);
	}

	public SHCameraSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SHCameraSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	void init(Context context) {
		mContext = context;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// 표면 생성시 카메라 오픈하고 미리보기 설정
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//mCamera = Camera.open();
		mCamera=openFrontFacingCamera();
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setDisplayOrientation(90);//여기 맞나?
		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
		}
	}

    // 표면 파괴시 카메라도 파괴한다.
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	// 표면의 크기가 결정될 때 최적의 미리보기 크기를 구해 설정한다.
	// 나중에 여기서 얼마나 설정되는지 확인해본다. ---> openCV에 보내기 위해서.
    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		Camera.Parameters params = mCamera.getParameters();
        List<Size> arSize = params.getSupportedPreviewSizes();
        if (arSize == null) {
			params.setPreviewSize(width, height);
        } else {
	        int diff = 10000;
	        Size opti = null;
	        for (Size s : arSize) {
	        	if (Math.abs(s.height - height) < diff) {
	        		diff = Math.abs(s.height - height);
	        		opti = s;
	        		
	        	}
	        }
			params.setPreviewSize(opti.width, opti.height);
        }
        
        CameraPage SHCamera = (CameraPage)mContext;
        if (SHCamera.mPicWidth != -1) {
			params.setPictureSize(SHCamera.mPicWidth, SHCamera.mPicHeight);
        }
        
		mCamera.setParameters(params);
		mCamera.startPreview();
	}
    
    /*
     * Open the camera.  First attempt to find and open the front-facing camera.
     * If that attempt fails, then fall back to whatever camera is available.
     * @return a Camera object
     */
    
    private Camera openFrontFacingCamera()
    {
    	final String TAG="FFC";
    	
    	Camera camera = null;
    	// Look for front-facing camera, using the Gingerbread API.
    	// Java reflection is used for backwards compatibility with pre-Gingerbread APIs.
    	try {
    		Class<?> cameraClass = Class.forName("android.hardware.Camera");
    		Object cameraInfo = null;
    		Field field = null;
    		int cameraCount = 0;
    		Method getNumberOfCamerasMethod = cameraClass.getMethod( "getNumberOfCameras" );
    		if ( getNumberOfCamerasMethod != null ) {
    			cameraCount = (Integer) getNumberOfCamerasMethod.invoke( null, (Object[]) null );
    		}
    		Class<?> cameraInfoClass = Class.forName("android.hardware.Camera$CameraInfo");
    		if ( cameraInfoClass != null ) {
    			cameraInfo = cameraInfoClass.newInstance();
    		}
    		if ( cameraInfo != null ) {
    			field = cameraInfo.getClass().getField( "facing" );
    		}
    		Method getCameraInfoMethod = cameraClass.getMethod( "getCameraInfo", Integer.TYPE, cameraInfoClass );
    		if ( getCameraInfoMethod != null && cameraInfoClass != null && field != null ) {
    			for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
    				getCameraInfoMethod.invoke( null, camIdx, cameraInfo );
    				int facing = field.getInt( cameraInfo );
    				if ( facing == 1 ) {
    					// Camera.CameraInfo.CAMERA_FACING_FRONT
    					try {
    						Method cameraOpenMethod = cameraClass.getMethod( "open", Integer.TYPE );
    						if ( cameraOpenMethod != null ) {
    							camera = (Camera) cameraOpenMethod.invoke( null, camIdx );
    						}
    					} catch (RuntimeException e) {
    						Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
    					}
    				}
    			}
    		}
    	}
    	// Ignore the bevy of checked exceptions the Java Reflection API throws - if it fails, who cares.
    	catch ( ClassNotFoundException e ) { Log.e(TAG, "ClassNotFoundException" + e.getLocalizedMessage());}
    	catch ( NoSuchMethodException e ) {Log.e(TAG, "NoSuchMethodException" + e.getLocalizedMessage());}
    	catch ( NoSuchFieldException e ) {Log.e(TAG, "NoSuchFieldException" + e.getLocalizedMessage());}
    	catch ( IllegalAccessException e ) {Log.e(TAG, "IllegalAccessException" + e.getLocalizedMessage());}
    	catch ( InvocationTargetException e ) {Log.e(TAG, "InvocationTargetException" + e.getLocalizedMessage());}
    	catch ( InstantiationException e ) {Log.e(TAG, "InstantiationException" + e.getLocalizedMessage());}
    	catch ( SecurityException e ) {Log.e(TAG, "SecurityException" + e.getLocalizedMessage());}
    	if ( camera == null ) {
    		// Try using the pre-Gingerbread APIs to open the camera.
    		try {
    			camera = Camera.open();         
    		} catch (RuntimeException e) {
    			Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
    		}
    	}
    	return camera; 
    } 
}