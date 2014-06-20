package fortyonepost.com.ag;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import fortyonepost.com.ag.Helper.NotificationSounds;

public class RotationMatrixTest extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private float[] mValuesAccelerometer = new float[3];
    private float[] mValuesMagnet = new float[3];
    private float[] rotationMatrix;
    private TextView azimuth;
    private float norm_Of_g;
    private ProgressBar waterLevelInGlassProgressBar;
    private final int DEFAULT_WATER_LEVEL = 0;
    final float[] mValuesOrientation = new float[3];
    private final int DEFAULT_HEAD_POSITION_ANGLE = 90;
    private final int CALIBRATION_ANGLE = 50;
    private final int HEAD_POSITION_DOWN = DEFAULT_HEAD_POSITION_ANGLE - CALIBRATION_ANGLE;
    private final int HEAD_POSITION_UP = DEFAULT_HEAD_POSITION_ANGLE + CALIBRATION_ANGLE;
    private final int PROGRESS_BAR_SCALE_FACTOR = (50 / (CALIBRATION_ANGLE));
    public static final int TAP = 13;
    public static final int DISALLOWED = 10;
    private String TAG = "fortyonepost.com.ag.RotationMatrixTest";

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
            mValuesMagnet[0] = (mValuesMagnet[0] * 1 + evt.values[0]) * 0.5f;
            mValuesMagnet[1] = (mValuesMagnet[1] * 1 + evt.values[1]) * 0.5f;
            mValuesMagnet[2] = (mValuesMagnet[2] * 1 + evt.values[2]) * 0.5f;
        } else if (type == Sensor.TYPE_ACCELEROMETER) {


            mValuesAccelerometer[0] = (mValuesAccelerometer[0] * 2 + evt.values[0]) * 0.33334f;
            //values[0]: Azimuth, rotation around the Z axis (0<=azimuth<360).
            // 0 = North, 90 = East, 180 = South, 270 = West


            mValuesAccelerometer[1] = (mValuesAccelerometer[1] * 2 + evt.values[1]) * 0.33334f;
            //values[1]: Pitch, rotation around X axis
            // (-180<=pitch<=180), with positive values when the z-axis moves toward the y-axis.


            mValuesAccelerometer[2] = (mValuesAccelerometer[2] * 2 + evt.values[2]) * 0.33334f;
            //values[2]: Roll, rotation around Y axis (-90<=roll<=90),
            // with positive values when the z-axis moves toward the x-axis.
            ;
        }

        if ((type==Sensor.TYPE_MAGNETIC_FIELD) || (type==Sensor.TYPE_ACCELEROMETER)) {
            rotationMatrix = new float[16];
            SensorManager.getRotationMatrix(rotationMatrix, null, mValuesAccelerometer, mValuesMagnet);
            SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_X,
                    rotationMatrix );
        }
        SensorManager.getOrientation(rotationMatrix, mValuesOrientation);

        int tiltZ = (int) Math.floor(Math.toDegrees(mValuesOrientation[0]));//azimuth
        int tiltX = (int) Math.floor(Math.toDegrees(mValuesOrientation[1])); //pitch
        int tiltY = (int) Math.floor(Math.toDegrees(mValuesOrientation[2])); //roll
        //  String test = "results New: " +tiltX +" "+tiltY+ " "+ tiltZ;
        //   Log.d(TAG, test);

        int inclination = ((tiltY - (HEAD_POSITION_DOWN))) * PROGRESS_BAR_SCALE_FACTOR;


        if (inclination % 5 == 0) {

            waterLevelInGlassProgressBar.setProgress(inclination);
            azimuth.setText("" + waterLevelInGlassProgressBar.getProgress());

        }


    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}


