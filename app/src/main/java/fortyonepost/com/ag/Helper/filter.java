package fortyonepost.com.ag.Helper;

/**
 * Created by asingh on 6/18/2014.
 */
public class filter {
    private static final boolean ADAPTIVE_ACCEL_FILTER = true;
    private static float lastAccel[] = new float[3];
    private static float accelFilter[] = new float[3];
    private static float filteredValues[] = new float[3];


    public static float[] filterAccelerometerData(float accelX, float accelY, float accelZ) {
        // high pass filter
        float updateFreq = 30; // match this to your update speed
        float cutOffFreq = 0.9f;
        float RC = 1.0f / cutOffFreq;
        float dt = 1.0f / updateFreq;
        float filterConstant = RC / (dt + RC);
        float alpha = filterConstant;
        float kAccelerometerMinStep = 0.033f;
        float kAccelerometerNoiseAttenuation = 3.0f;

        if (ADAPTIVE_ACCEL_FILTER) {
            float d = Mathematics.clamp(Math.abs(Mathematics.norm(accelFilter[0], accelFilter[1], accelFilter[2]) - Mathematics.norm(accelX, accelY, accelZ)) / kAccelerometerMinStep - 1.0f, 0.0f, 1.0f);
            alpha = d * filterConstant / kAccelerometerNoiseAttenuation + (1.0f - d) * filterConstant;
        }

        accelFilter[0] = (alpha * (accelFilter[0] + accelX - lastAccel[0]));
        accelFilter[1] = (alpha * (accelFilter[1] + accelY - lastAccel[1]));
        accelFilter[2] = (alpha * (accelFilter[2] + accelZ - lastAccel[2]));

        lastAccel[0] = accelX;
        lastAccel[1] = accelY;
        lastAccel[2] = accelZ;

        filteredValues[0] = accelFilter[2];
        filteredValues[1] = accelFilter[1];
        filteredValues[2] = accelFilter[0];

        return filteredValues;
        //onFilteredAccelerometerChanged(accelFilter[0], accelFilter[1], accelFilter[2]);
    }

}
