import com.alibaba.fastjson.JSON;
import com.enhtmv.sublib.common.http.SubHttp;
import com.enhtmv.sublib.common.http.SubResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AndroidUtilsTest {

    public static void main(String[] args) throws Exception {


        SubHttp http = new SubHttp();


        SubResponse response = http.get("http://www.baidu.com");


        System.out.println(JSON.toJSONString(response));

    }
}
