package curve.zoey.hientran.metawearguide.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by hientran on 2/2/17.
 */

public class Util {
    public Util() {
    }

    public Double getMS(Double[] array){
        Double[] nums = array;
        Double returnVal = 0.0;
        for (Double num : nums) {
            returnVal += num;
        }
        returnVal = returnVal/nums.length;
        return returnVal;
    }

    public Double meanSquare(Double[] array) {
        double ms = 0;
        for (int i = 0; i < array.length; i++)
            ms += array[i] * array[i];
        ms /= array.length;
        return Math.sqrt(ms);
    }

    public Double[] avgAxisVals(Double[][] array){
        Double sum = 0.0;

        Double[] avgAxis = new Double[]{0.0, 0.0, 0.0};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < array.length; j++) {
                sum += array[j][i];
//                Log.i("AVG", "This is the value " +array[j][i]);
//                System.out.print("This is the value: "+array[j][i]);
            }
            avgAxis[i] = sum/array.length;
            sum = 0.0;
        }

        System.out.println("Avg Axis Set is: " + ArrayUtils.toString(avgAxis));

        return avgAxis;
    }

    public Double[] rmsAxisVals(Double[][] array){
        Double sum = 0.0;

        Double[] rmsAxis = new Double[]{0.0, 0.0, 0.0};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < array.length; j++) {
                sum += (array[j][i] * array[j][i]);
//                Log.i("AVG", "This is the value " +array[j][i]);
//                System.out.print("This is the value: "+array[j][i]);
            }
            rmsAxis[i] = Math.sqrt(sum/array.length);
            sum = 0.0;
        }

        System.out.println("RMS Axis Set is: " + ArrayUtils.toString(rmsAxis));
        return rmsAxis;
    }

    public Double[][] combineToFeatures(Double[][] accels, Double[][] gyros){
        int accel_length = accels.length;
        int gyro_length = gyros.length;

        System.out.println("Accel Length: " + accel_length);
        System.out.println("Gyro Length: " + gyro_length);

        int standard_length = (accel_length < gyro_length)? accel_length : gyro_length;

        System.out.println("Standard Length: " + standard_length);

        Double[][] featuresSet = new Double[standard_length][accels[0].length+gyros[0].length];

        for (int i = 0; i < standard_length; i++) {
            featuresSet[i] = ArrayUtils.addAll(accels[i],gyros[i]);
            //System.out.print("Feature Set is: " + ArrayUtils.toString(featuresSet[i])));
        }
        System.out.println("Feature Set is: " + ArrayUtils.toString(featuresSet));

        return featuresSet;
    }


}
