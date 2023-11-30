package com.alibaba.aie.params;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 创建AISeg任务请求
 */
public class CreateAiesegJobRequest {
    private final Logger logger = LoggerFactory.getLogger(CreateAiesegJobRequest.class);

    /**
     * AiesegJobType实例变量，表示Aieseg的工作类型。
     */
    private AiesegJobType aiesegJobType;

    /**
     * AbstractParam实例变量，表示输入参数。
     */
    private AbstractParam input;

    /**
     * String实例变量，表示工作名称。
     */
    private String jobName;

    /**
     * Long实例变量，表示项目ID。
     */
    private Long projectId;

    /**
     * String实例变量，用于过滤形状数据ID。
     */
    private String filterShapeDataId;

    /**
     * String实例变量，用于过滤形状的WKT（Well-Known Text）表示。
     */
    private String filterShapeWkt;

    /**
     * Integer实例变量，表示像素阈值。
     */
    private Integer pixelThreshold;

    /**
     * String实例变量，表示视觉提示ID。
     */
    private String visualPromptId;

    /**
     * List<String>实例变量，表示文本提示列表。
     */
    private List<String> textPrompt;


    public void validate() {
        validateJobName();

        warnIfFilterShapeDataIdAndWktExists();

        warnIfPanopticJobHasIgnoredFields();

        warnIfTextPromptHasMoreThanOneChar();

        warnIfPromptJobHasMoreThanOneField();
    }

    private void warnIfFilterShapeDataIdAndWktExists() {
        if (filterShapeDataId != null && filterShapeWkt != null) {
            logger.warn(
                    "参数 filter_shape_data_id 与 filter_shape_wkt 只需提供一个，当两个参数都不为空时，默认使用 filter_shape_data_id");
        }
    }

    private void warnIfPanopticJobHasIgnoredFields() {
        if (aiesegJobType == AiesegJobType.AIE_SEG_PANOPTIC &&
                (pixelThreshold != null || visualPromptId != null || textPrompt != null)) {
            logger.warn(
                    "当使用 " + AiesegJobType.AIE_SEG_PANOPTIC + " 时，参数 pixel_threshold, visual_prompt_id, text_prompt 会被忽略");
        }
    }

    private void warnIfTextPromptHasMoreThanOneChar() {
        if (textPrompt != null && textPrompt.size() > 1) {
            logger.warn("参数 text_prompt 目前仅支持第一个，更多支持正在迭代中...");
        }
    }

    private void warnIfPromptJobHasMoreThanOneField() {
        if (aiesegJobType == AiesegJobType.AIE_SEG_PROMPT && visualPromptId != null && textPrompt != null) {
            logger.warn("当使用" + AiesegJobType.AIE_SEG_PROMPT + "时，visual_prompt_id, text_prompt 只需提供一个，当两个参数都不为空时，优先使用 visual_prompt_id");
        }
    }

    private void validateJobName() {
        if (jobName == null || jobName.trim().isEmpty()) {
            throw new IllegalArgumentException("job_name 不可为空");
        }
    }

    public AiesegJobType getAiesegJobType() {
        return aiesegJobType;
    }

    public void setAiesegJobType(AiesegJobType aiesegJobType) {
        this.aiesegJobType = aiesegJobType;
    }

    public AbstractParam getInput() {
        return input;
    }

    public void setInput(AbstractParam input) {
        this.input = input;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getFilterShapeDataId() {
        return filterShapeDataId;
    }

    public void setFilterShapeDataId(String filterShapeDataId) {
        this.filterShapeDataId = filterShapeDataId;
    }

    public String getFilterShapeWkt() {
        return filterShapeWkt;
    }

    public void setFilterShapeWkt(String filterShapeWkt) {
        this.filterShapeWkt = filterShapeWkt;
    }

    public Integer getPixelThreshold() {
        return pixelThreshold;
    }

    public void setPixelThreshold(Integer pixelThreshold) {
        this.pixelThreshold = pixelThreshold;
    }

    public String getVisualPromptId() {
        return visualPromptId;
    }

    public void setVisualPromptId(String visualPromptId) {
        this.visualPromptId = visualPromptId;
    }

    public List<String> getTextPrompt() {
        return textPrompt;
    }

    public void setTextPrompt(List<String> textPrompt) {
        this.textPrompt = textPrompt;
    }

    public static abstract class AbstractParam {
        protected String dataId;

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }
    }

    public static class RasterParam extends AbstractParam {
        protected String bandName;

        public String getBandName() {
            return bandName;
        }

        public void setBandName(String bandName) {
            this.bandName = bandName;
        }
    }

    public static class MapServiceParam extends AbstractParam {
        protected Integer zoomLevel;

        public Integer getZoomLevel() {
            return zoomLevel;
        }

        public void setZoomLevel(Integer zoomLevel) {
            this.zoomLevel = zoomLevel;
        }
    }

}
