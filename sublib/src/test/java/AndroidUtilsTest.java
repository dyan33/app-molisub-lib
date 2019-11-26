import java.util.TimeZone;

public class AndroidUtilsTest {

    public static void main(String[] args) throws Exception {


        TimeZone zone = TimeZone.getDefault();

        System.out.println(zone.getID());

    }
}
