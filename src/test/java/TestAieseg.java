import com.alibaba.aie.ExtClient;
import com.alibaba.aie.params.AiesegJobType;
import com.alibaba.aie.params.CreateAiesegJobRequest;

import java.util.ArrayList;
import java.util.List;

public class TestAieseg {

    public static void main(String[] args) throws Exception {
        testTextPrompt();
        testVisualPromopt();
        testPanoptic();
    }


    public static void testTextPrompt() throws Exception {

        CreateAiesegJobRequest create_aieseg_request = new CreateAiesegJobRequest();
        create_aieseg_request.setAiesegJobType(AiesegJobType.AIE_SEG_PROMPT);

        CreateAiesegJobRequest.RasterParam rasterParam = new CreateAiesegJobRequest.RasterParam();
        rasterParam.setDataId("a8416d6081d3411b75ca449edd21f0fa");
        create_aieseg_request.setInput(rasterParam);

        create_aieseg_request.setJobName("test_AiesegJobTypeAIE_SEG_PROMPT");
        create_aieseg_request.setPixelThreshold(2);
        List<String> text_prompt = new ArrayList<>();
        text_prompt.add("油罐");
        create_aieseg_request.setTextPrompt(text_prompt);


        ExtClient extClient = new ExtClient(System.getenv("ALIYUN_AK_ID"),
                System.getenv("ALIYUN_AK_SECRET"));
        extClient.createAiesegJob(create_aieseg_request);
    }

    public static void testVisualPromopt() throws Exception {
        CreateAiesegJobRequest create_aieseg_request = new CreateAiesegJobRequest();
        create_aieseg_request.setAiesegJobType(AiesegJobType.AIE_SEG_PROMPT);

        CreateAiesegJobRequest.RasterParam rasterParam = new CreateAiesegJobRequest.RasterParam();
        rasterParam.setDataId("a8416d6081d3411b75ca449edd21f0fa");
        create_aieseg_request.setInput(rasterParam);

        create_aieseg_request.setJobName("nihao");
        create_aieseg_request.setPixelThreshold(2);
        create_aieseg_request.setVisualPromptId("96401692670748344");

        ExtClient extClient = new ExtClient(System.getenv("ALIYUN_AK_ID"),
                System.getenv("ALIYUN_AK_SECRET"));
        extClient.createAiesegJob(create_aieseg_request);
    }

    public static void testPanoptic() throws Exception {
        CreateAiesegJobRequest create_aieseg_request = new CreateAiesegJobRequest();
        create_aieseg_request.setAiesegJobType(AiesegJobType.AIE_SEG_PANOPTIC);
        create_aieseg_request.setJobName("nihao2");

        CreateAiesegJobRequest.RasterParam rasterParam = new CreateAiesegJobRequest.RasterParam();
        rasterParam.setDataId("a8416d6081d3411b75ca449edd21f0fa");
        create_aieseg_request.setInput(rasterParam);


        ExtClient extClient = new ExtClient(System.getenv("ALIYUN_AK_ID"),
                System.getenv("ALIYUN_AK_SECRET"));
        extClient.createAiesegJob(create_aieseg_request);
    }
}
