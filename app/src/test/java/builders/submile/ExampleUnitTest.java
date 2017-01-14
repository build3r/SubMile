package builders.submile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
    @Test
    public void addition_isCorrect() throws Exception
    {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test_gsonSerialize () throws Exception{
        GeoLocation [] waypoints = new GeoLocation[3];
        waypoints[0] = new GeoLocation(125, 125);
        waypoints[1] = new GeoLocation(150, 150);
        waypoints[2] = new GeoLocation(175, 175);
        PathData path = new PathData("1234567890", "3, abc, def", waypoints, new GeoLocation(100, 100), new GeoLocation(200, 200));
        String json = gsonUtil.tojson(path);
        System.out.println(json);
    }
    @Test
    public void test_gsonDeSerialize () throws Exception{
        String json = "{\"mobileNumber\":\"1234567890\",\"address\":\"3, abc, def\",\"wayPoints\":[{\"lat\":125.0,\"lng\":125.0},{\"lat\":150.0,\"lng\":150.0},{\"lat\":175.0,\"lng\":175.0}],\"start\":{\"lat\":100.0,\"lng\":100.0},\"end\":{\"lat\":200.0,\"lng\":200.0}}";
        Gson gson = new GsonBuilder().create();
        PathData path = gson.fromJson(json, PathData.class);
        System.out.println(path);
    }
}
