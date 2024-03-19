import com.alibaba.aie.ExtClient;
import com.alibaba.aie.params.PublishLocalImgIgeRequest;
import com.alibaba.aie.params.PublishLocalImgRequest;
import com.alibaba.aie.params.PublishLocalShapefileRequest;
import com.alibaba.aie.params.PublishLocalTiffRequest;
import com.alibaba.aie.params.PublishLocalTiffRpbRequest;
import com.alibaba.aie.params.PublishLocalTiffRpcRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/18
 **/
public class PublishLocalImgTest {

//    static {
//        System.setProperty("SDK_CLIENT_HOST", "https://pre-engine-aiearth.aliyun.com");
//        System.setProperty("OPENAPI_ENDPOINT", "aiearth-engine-pre.cn-hangzhou.aliyuncs.com");
//    }

    private ExtClient client;

    @Before
    public void setup() throws Exception {
        client = new ExtClient(System.getenv("ALIYUN_AK_ID"),
                System.getenv("ALIYUN_AK_SECRET"));
    }

    @Test
    public void testPublishTiff() throws Throwable {
        String localFilePath = "/Users/songci/Desktop/98610226-1706087106445-8674319.tif";
        PublishLocalTiffRequest request = new PublishLocalTiffRequest();
        request.setLocalFilePath(localFilePath);
        request.setName("test_publish_local_tiff");
        client.publishLocalTiff(request);
    }

    @Test
    public void testPublishSingleImg() throws Throwable {
        String localFilePath = "/Users/songci/Desktop/q.img";
        PublishLocalImgRequest request = new PublishLocalImgRequest();
        request.setLocalFilePath(localFilePath);
        request.setName("test_publish_local_img");
        client.publishLocalImg(request);
    }

    @Test
    public void testPublishSingleShapefile() throws Throwable {
        String localFilePath = "/Users/songci/Downloads/lcc.zip";
        PublishLocalShapefileRequest request = new PublishLocalShapefileRequest();
        request.setLocalFilePath(localFilePath);
        request.setName("test_publish_local_shapefile");
        client.publishLocalShapefile(request);
    }

    @Test
    public void testPublishImgIge() throws Throwable {
        PublishLocalImgIgeRequest request = new PublishLocalImgIgeRequest();
        request.setMainFilePath("/Users/songci/Desktop/0_X0_Y0_W30000_H30000.img");
        request.setAttachFilePath("/Users/songci/Desktop/0_X0_Y0_W30000_H30000.ige");
        request.setName("test_publish_local_img_ige");
        request.setTaskNum(10);
        client.publishLocalImgIge(request);
    }

    @Test
    public void testPublishTiffRpc() throws Throwable {
        PublishLocalTiffRpcRequest request = new PublishLocalTiffRpcRequest();
        request.setMainFilePath("/Users/songci/Desktop/344-1688992162646-5697952.tif/344-1688992162646-5697952.tif");
        request.setAttachFilePath("/Users/songci/Desktop/344-1688992162646-5697952.tif/344-1688992162646-5697952_rpc.txt");
        request.setName("test_publish_local_tiff_rpc");
        request.setTaskNum(4);
        client.publishLocalTiffRpc(request);
    }

    @Test
    public void testPublishTiffRpb() throws Throwable {
        PublishLocalTiffRpbRequest request = new PublishLocalTiffRpbRequest();
        request.setMainFilePath("/Users/songci/Desktop/344-1688991984444-6577223.tif/344-1688991984444-6577223.tif");
        request.setAttachFilePath("/Users/songci/Desktop/344-1688991984444-6577223.tif/344-1688991984444-6577223.rpb");
        request.setName("test_publish_local_tiff_rpb");
        request.setTaskNum(4);
        client.publishLocalTiffRpb(request);
    }

}
