package wonbin.BetaTest;

import android.app.*;
import android.os.*;
import android.view.*;

public class LoadingPage extends Activity
{
    @Override
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loadingpage);
        
        Handler handler=new Handler(){
        	@Override
			public void handleMessage(Message msg)
        	{
        		finish();
        	}
        };
        
        handler.sendEmptyMessageDelayed(0, 3000);
    }
}