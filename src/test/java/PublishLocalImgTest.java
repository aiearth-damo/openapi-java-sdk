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

    private ExtClient client;

    @Before
    public void setup() throws Exception {
        client = new ExtClient(System.getenv("ALIYUN_AK_ID"),
                System.getenv("ALIYUN_AK_SECRET"));
    }

    @Test
    public void testPublishTiff() throws Throwable {
        String localFilePath = "/path/to/file.tif";
        PublishLocalTiffRequest request = new PublishLocalTiffRequest();
        request.setLocalFilePath(localFilePath);
        request.setName("test_publish_local_tiff");
        client.publishLocalTiff(request);
    }

    @Test
    public void testPublishSingleImg() throws Throwable {
        String localFilePath = "/path/to/file.img";
        PublishLocalImgRequest request = new PublishLocalImgRequest();
        request.setLocalFilePath(localFilePath);
        request.setName("test_publish_local_img");
        client.publishLocalImg(request);
    }

    @Test
    public void testPublishSingleShapefile() throws Throwable {
        String localFilePath = "/path/to/file.zip";
        PublishLocalShapefileRequest request = new PublishLocalShapefileRequest();
        request.setLocalFilePath(localFilePath);
        request.setName("test_publish_local_shapefile");
        client.publishLocalShapefile(request);
    }

    @Test
    public void testPublishImgIge() throws Throwable {
        PublishLocalImgIgeRequest request = new PublishLocalImgIgeRequest();
        request.setMainFilePath("/path/to/file.img");
        request.setAttachFilePath("/path/to/file.ige");
        request.setName("test_publish_local_img_ige");
        request.setTaskNum(10);
        client.publishLocalImgIge(request);
    }

    @Test
    public void testPublishTiffRpc() throws Throwable {
        PublishLocalTiffRpcRequest request = new PublishLocalTiffRpcRequest();
        request.setMainFilePath("/path/to/file.tif");
        request.setAttachFilePath("/path/to/file.txt");
        request.setName("test_publish_local_tiff_rpc");
        request.setTaskNum(4);
        client.publishLocalTiffRpc(request);
    }

    @Test
    public void testPublishTiffRpb() throws Throwable {
        PublishLocalTiffRpbRequest request = new PublishLocalTiffRpbRequest();
        request.setMainFilePath("/path/to/file.tif");
        request.setAttachFilePath("/path/to/file.rpb");
        request.setName("test_publish_local_tiff_rpb");
        request.setTaskNum(4);
        client.publishLocalTiffRpb(request);
    }

}
