import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AndroidUtilsTest {

    public static void main(String[] args) throws Exception {


        SubHttp http = new SubHttp();

        SubResponse response = http.get("http://offer.allcpx.com/offer/track?offer=271&clickId=");

        System.out.println(response.flowUrls());

    }
}
