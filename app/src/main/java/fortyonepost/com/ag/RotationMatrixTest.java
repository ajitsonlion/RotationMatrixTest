package fortyonepost.com.ag;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RotationMatrixTest extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private float[] gravity = new float[3];
    private float[] geomag = new float[3];
    private float[] rotationMatrix;
    private TextView azimuth;
    private float norm_Of_g;
    private ProgressBar waterLevelInGlassProgressBar;
    private final int DEFAULT_WATER_LEVEL=50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);



        azimuth=(TextView)findViewById(R.id.azimuth);
        //  roll=(TextView)findViewById(R.id.roll);
        //pitch=(TextView)findViewById(R.id.pitch);
        waterLevelInGlassProgressBar=(ProgressBar)findViewById(R.id.vertical_progressbar);
        waterLevelInGlassProgressBar.setMax(100);
        waterLevelInGlassProgressBar.setProgress(DEFAULT_WATER_LEVEL);





    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME );
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent evt) {
        int type=evt.sensor.getType();

        //Smoothing the sensor data a bit
        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomag[0]=(geomag[0]*1+evt.values[0])*0.5f;
            geomag[1]=(geomag[1]*1+evt.values[1])*0.5f;
            geomag[2]=(geomag[2]*1+evt.values[2])*0.5f;
        } else if (type == Sensor.TYPE_ACCELEROMETER) {

            float[]  g = evt.values.clone();
            norm_Of_g = (float)(Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]));


            g[0] = g[0] / norm_Of_g;
            g[1] = g[1] / norm_Of_g;
            g[2] = g[2] / norm_Of_g;

            gravity[0]=(gravity[0]*2+evt.values[0])*0.33334f;
            //values[0]: Azimuth, rotation around the Z axis (0<=azimuth<360).
            // 0 = North, 90 = East, 180 = South, 270 = West
            int inclinationZ = (int) Math.round(Math.toDegrees(Math.acos(gravity[0])));


            int finalWaterLevel=inclinationZ-DEFAULT_WATER_LEVEL;
            waterLevelInGlassProgressBar.setProgress(finalWaterLevel);

            azimuth.setText(""+waterLevelInGlassProgressBar.getProgress());



            gravity[1]=(gravity[1]*2+evt.values[1])*0.33334f;
            //values[1]: Pitch, rotation around X axis
            // (-180<=pitch<=180), with positive values when the z-axis moves toward the y-axis.
            //  int inclinationX = (int) Math.round(Math.toDegrees(Math.acos(gravity[1])));
            // pitch.setText(""+inclinationX);


            gravity[2]=(gravity[2]*2+evt.values[2])*0.33334f;
            //values[2]: Roll, rotation around Y axis (-90<=roll<=90),
            // with positive values when the z-axis moves toward the x-axis.
            //  int inclinationY = (int) Math.round(Math.toDegrees(Math.acos(gravity[2])));
            // roll.setText("" + inclinationY);
        }

        if ((type==Sensor.TYPE_MAGNETIC_FIELD) || (type==Sensor.TYPE_ACCELEROMETER)) {
            rotationMatrix = new float[16];
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomag);
            SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_X,
                    rotationMatrix );
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}