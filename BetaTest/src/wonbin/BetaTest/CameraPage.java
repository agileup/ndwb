/*
 * �ϴ� ���� Ʋ�� ���� ������Ʈ�� ����Դµ� ���⼭ ���� ���� �� ���ߵȴ�.
 * 
 * --- ����, min 2.2�� �����. 11�� 5���� �������Ƿ� �� �̻����� �ϱ� �� �׷��� �׷� �ʿ䵵 ����.
 *     ���� ī�޶� ������ 2.3�̻��̾�� �ϴµ�, �ϴ� ��� ���� �����Ƿ� ���д�.
 * --- ���� ��� ���ش�.
 * --- autofocus �����ص� ������ش�.
 * --- �ٹ��� ��ģ��. - Activity �ϳ� ����� �Կ� �Ŀ��� Intent�� �����ֵ��� �Ѵ�.
 * ���̾ƿ� ��ģ��.
 * portrait�� �ٲ۴�.
 * ���� �̹����� ���� ��ģ��.
 * �Կ��Ҹ� �ִ´�.(���� �����ϰ� �صд�?)(�ؿ��� �Ҹ�����...)
 * --- ��ġ, ����ġ �߰�
 * ���� ��õ ���̾ƿ� �ֱ�
 * --- ����ī�޶� default��
 * --- ������ ������ �̹��� ���� - �߿��ϸ�, ������ �ʿ�����, ���������̳� �ν� ����Ȯ���� ���� �Ϻ��� �ν��� �ȵ� ��츦 ����� ������ ������ �� ���� ����غ��� �Ѵ�.
 * 							�ϴ� ��� �ּ�ó���صд�.(���� �Ⱦ��� ���� ��� ��� ����)
 * �ٹ� ����� �����ؼ��� �� �˾ƺ��� �Ѵ�.
 * --- �ð����� pref���ִ��� ���캻��. => �ػ� �����
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
		
		// SD ī�尡 ���� �� ���� ó���Ѵ�.
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
			Toast.makeText(this, "SD ī�� ����", 1).show();
			finish();
			return;
		}
		
		mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/" + PICFOLDER;
		File fRoot = new File(mRootPath);
		if (fRoot.exists() == false) {
			if (fRoot.mkdir() == false) {
				Toast.makeText(this, "���� ���� ����", 1).show();
				finish();
				return;
			}
		}
		
		// �����۷������� ũ�� �о� ��
		SharedPreferences pref = getSharedPreferences("SHCamera",0);
		mPicWidth = pref.getInt("PicWidth", -1);
		mPicHeight = pref.getInt("PicHeight", -1);
		
		// ��ư���� Ŭ�� ������ ����
		mTakePicture = (FrameLayout)findViewById(R.id.takepicture);
		mSurface = (SHCameraSurface)findViewById(R.id.preview);
		
		//touch ����
		mSurface.setOnClickListener(mClickListener);
		mSurface.setOnLongClickListener(mLongClickListener);
	}
	
	View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//������ �̵����� ����Ѵ�. ---> processor���� ���Ѵ�.
			//������ �̵��� ������ ������ �غ��Ѵ�. ---> sensor�κп��� ó��
			//������ �̵������� ǥ���Ѵ�. �׷��鼭 �̵��� ��� üũ�Ѵ�.
			Toast.makeText(CameraPage.this, "��ġ", 1).show();
		}
	};
	
	View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			mSurface.mCamera.autoFocus(mAutoFocus);
			//Toast.makeText(CameraTestActivity.this, "����ġ", 1).show();
			return true;
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	// ��Ŀ�� �����ϸ� �Կ�
	AutoFocusCallback mAutoFocus = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera)
		{
			mTakePicture.postDelayed(new Runnable(){
				@Override
				public void run()
				{
					mSurface.mCamera.takePicture(null, null, mPicture); // ------ ���߿� �ٽ� ���� (�̹��� ������ �Ҽ��� ����)
				}
			}, TAKEDELAY);// delay�� �Ƹ� �� �Լ� return �ð��� �� �����Ƿ�, success���ο� ������� ����ϵ��� �Ѵ�.
			// ���߿� focusing���� visualization(�ʷ�, ���� ������)�� �ϵ��� �Ѵ�.
		}
	};

	// ���� ����. ��¥�� �ð����� ���ϸ� �����ϰ� ������ �̵�� ��ĵ ����
	// --- ���⼭�� 90�� ������ ��������� �Ѵ�.
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
				Toast.makeText(mMainContext, "���� ���� �� ���� �߻� : " + e.getMessage(), 0).show();
				return;
			}

			// ��ĳ�� ��û
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.parse("file://" + path);
			intent.setData(uri);
			sendBroadcast(intent);

			mLastPicture = path;
			
			//���⼭ �ϴ°� �´��� �������� �ٹ� �ҷ��ش�.(postview������ �ǰ����� ���� �İ� �� ����, back�ϸ� �ؿ� startPreview�ǵ���.)
			//(���� project�� �������־ �ӽ÷� picturepage�� �����س��Ҵ�.)
			intent = new Intent(CameraPage.this, PicturePage.class);
			intent.putExtra("path", mLastPicture);
			startActivity(intent);
			
			//Ȥ�� ������� ���ƿԴ��� 90�� ���ư��ִٸ� ���⵵ setDisplayOrientation(90); ���ش�.
			mSurface.mCamera.startPreview();
		}
	};
	
	//���� �ػ� ������ ���ϰ� �޴��� �ϴ� ù��° �ɷ� �־���´�.(�� ���� �ػ�)
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
			.setTitle("���� �ػ� ����")
			.setSingleChoiceItems(arName, mSelect, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelect = which;
				}
			})
			.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
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
			.setNegativeButton("���", null)
			.show();
		}
	};
}

// �̸����� ǥ�� Ŭ����
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

	// ǥ�� ������ ī�޶� �����ϰ� �̸����� ����
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//mCamera = Camera.open();
		mCamera=openFrontFacingCamera();
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setDisplayOrientation(90);//���� �³�?
		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
		}
	}

    // ǥ�� �ı��� ī�޶� �ı��Ѵ�.
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	// ǥ���� ũ�Ⱑ ������ �� ������ �̸����� ũ�⸦ ���� �����Ѵ�.
	// ���߿� ���⼭ �󸶳� �����Ǵ��� Ȯ���غ���. ---> openCV�� ������ ���ؼ�.
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