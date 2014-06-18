package fortyonepost.com.ag.Helper;

/**
 * Created by asingh on 6/18/2014.
 */
public class Mathematics {


    public static float clamp(float val, float min, float max) {
        return Math.max(min, java.lang.Math.min(max, val));
    }

    public static float norm(float x, float y, float z) {
        return (float) (Math.sqrt(x * x + y * y + z * z));
    }
}
