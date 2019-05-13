import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AndroidUtilsTest {

    public static void main(String[] args) {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        System.out.println(sdf.format(new Date()));

    }
}
