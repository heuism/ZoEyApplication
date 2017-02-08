package curve.zoey.hientran.metawearguide; /**
 * Created by hientran on 2/2/17.
 */

import org.junit.Test;
import curve.zoey.hientran.metawearguide.util.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

public class UtilTest {

    private Util util = new Util();

    @Test
    public void testRMS(){
        Double[] arr = {1.0, 2.0, 3.0, 4.0, 5.0};

        Double result = Math.sqrt(11.0);

        assertEquals("This is the RMS result",util.meanSquare(arr), result);
    }

    @Test
    public void testAvgAxisVals(){
        Double[][] array = {
                {1.0, 2.0, 3.0},
                {2.0, 3.0, 4.0},
                {3.0, 4.0, 5.0},
                {4.0, 5.0, 6.0}
        };

        Double[] retArr = {2.5, 3.5, 4.5};

        assertArrayEquals("This is the AvgAxis result", util.avgAxisVals(array), retArr);
    }

    @Test
    public void testRMSAxisVals(){
        Double[][] array = {
                {1.0, 2.0, 3.0},
                {2.0, 3.0, 4.0},
                {3.0, 4.0, 5.0},
                {4.0, 5.0, 6.0}
        };

        Double[] retArr = {Math.sqrt(30)/2, Math.sqrt(54)/2, Math.sqrt(86)/2};

        assertArrayEquals("This is the RMSAxis result", util.rmsAxisVals(array), retArr);
    }

    @Test
    public void testCombineToFeatures(){
        Double[][] accels = {
                {1.0, 2.0, 3.0, 4.0},
                {2.0, 3.0, 4.0, 5.0},
                {3.0, 4.0, 5.0, 6.0},
                {4.0, 5.0, 6.0, 7.0},
                {5.0, 6.0, 7.0, 8.0}
        };

        Double[][] gyros = {
                {5.0, 6.0, 7.0, 8.0},
                {6.0, 7.0, 8.0, 9.0},
                {7.0, 8.0, 9.0, 10.0},
                {8.0, 9.0, 10.0, 11.0}
        };

        Double[][] combineArray = {
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0},
                {2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0},
                {3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0},
                {4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0}
        };

        assertArrayEquals("This is CombineToFeatures result", util.combineToFeatures(accels, gyros), combineArray);
    }

    @Test
    public void testHighpassFilter(){
        Filter filter = new Filter(1,50, Filter.PassType.Highpass,1);
        Double[] array = {-0.908, -0.911, -0.908,  -0.64, -0.917, -1.037, -0.913, -1.021, -0.908, -0.823, -0.91, -1.189, -0.908, -1.524, -0.909, -1.448, -0.91, -1.143, -0.917};
        Double[] filteredArr = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            filter.updateValue(array[i]);
            filteredArr[i] = filter.getValue();
        }
        System.out.println("The array afater filter is: "+ Arrays.toString(filteredArr));
    }
}
