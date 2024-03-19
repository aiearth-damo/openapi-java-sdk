import com.alibaba.aie.ApiHelper;
import com.alibaba.aie.ExtClient;
import com.alibaba.aie.dtos.DataType;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

/**
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/18
 **/
public class ApiHelperTest {

    ExtClient extClient;

    @Before
    public void setup() throws Exception {
        extClient = new ExtClient(System.getenv("ALIYUN_AK_ID"),
                System.getenv("ALIYUN_AK_SECRET"));
    }

    @Test
    public void testApiHelperGetStsToken() throws Exception {

        Object o = ApiHelper.getStsToken(extClient, DataType.VECTOR, "zip", null);
        System.out.println(JSON.toJSONString(o));
    }

}
