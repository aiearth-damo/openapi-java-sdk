package com.alibaba.aie.params;

public enum AiesegJobType {

    AIE_SEG_PROMPT("aie_seg_prompt"),
    AIE_SEG_PANOPTIC("aie_seg_panoptic"),
    ;


    private final String code;

    public String getCode() {
        return code;
    }

    AiesegJobType(String code) {
        this.code = code;
    }

}
