package wonbin.BetaTest;

import android.app.*;
import android.os.*;
import android.view.*;

public class OptionPage extends Activity
{
    @Override
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.optionpage);
    }
}