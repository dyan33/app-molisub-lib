import com.alibaba.fastjson.JSON;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AndroidUtilsTest {

    public static void main(String[] args) throws Exception {


        TimeZone zone = TimeZone.getDefault();

        System.out.println(zone.getID());

    }
}
