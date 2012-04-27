package wonbin.SensorTest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorTestActivity extends Activity implements SensorEventListener
{
	SensorManager sensorManager;
	Sensor accSensor, lightSensor, tempSensor, gyroSensor, magSensor, pressSensor, proxSensor, oriSensor;
	TextView txtAll, txtAcc, txtLight, txtTemp, txtGyro, txtMag, txtPress, txtProx, txtOri;
	int a[];

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		a=new int[8];
		for(int i=0; i<8; i++) a[i]=0;
		
		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		
		accSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		lightSensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		tempSensor=sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
		gyroSensor=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		magSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		pressSensor=sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		proxSensor=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		oriSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		txtAll=(TextView)findViewById(R.id.txtAll);
		txtAcc=(TextView)findViewById(R.id.txtAcc);
		txtLight=(TextView)findViewById(R.id.txtLight);
		txtTemp=(TextView)findViewById(R.id.txtTemp);
		txtGyro=(TextView)findViewById(R.id.txtGyro);
		txtMag=(TextView)findViewById(R.id.txtMag);
		txtPress=(TextView)findViewById(R.id.txtPress);
		txtProx=(TextView)findViewById(R.id.txtProx);
		txtOri=(TextView)findViewById(R.id.txtOri);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, pressSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		sensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		synchronized(this)
		{
			a[event.sensor.getType()-1]=1;
			txtAll.setText(a[0]+" "+a[1]+" "+a[2]+" "+a[3]+" "+a[4]+" "+a[5]+" "+a[6]+" "+a[7]);
			
			switch(event.sensor.getType())
			{
			case Sensor.TYPE_ACCELEROMETER:
				txtAcc.setText("ACC => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			case Sensor.TYPE_LIGHT:
				txtLight.setText("LIGHT => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			case 13:
				txtTemp.setText("TEMP => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			case Sensor.TYPE_GYROSCOPE:
				txtGyro.setText("GYRO => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				txtMag.setText("MAG => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			case Sensor.TYPE_PRESSURE:
				txtPress.setText("PRESS => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			case Sensor.TYPE_PROXIMITY:
				txtProx.setText("PROX => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			case Sensor.TYPE_ORIENTATION:
				txtProx.setText("ORI => "+event.values[0]+", "+event.values[1]+", "+event.values[2]);
				break;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}
}