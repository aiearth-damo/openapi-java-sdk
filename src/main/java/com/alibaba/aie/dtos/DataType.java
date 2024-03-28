package com.alibaba.aie.dtos;

/**
 * 数据类型
 *
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/18
 **/
public enum DataType {

    /**
     * 栅格
     */
    RASTER("raster"),

    /**
     * 矢量
     */
    VECTOR("vector"),

    /**
     * 地图服务
     */
    MAP_SERVICE("map_service"),

    /**
     * 数据集
     */
    DATASET("dataset"),

    AIESEG("aieseg"),
    ;

    private final String code;

    DataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
