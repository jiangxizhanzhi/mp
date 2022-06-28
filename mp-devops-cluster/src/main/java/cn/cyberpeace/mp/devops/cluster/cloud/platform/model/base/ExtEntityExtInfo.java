package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/28
 */
@Data
public class ExtEntityExtInfo {

    /**
     * 主机名
     */
    @ApiModelProperty("主机名")
    private String hostName;

    /**
     * 主机类型
     */
    @ApiModelProperty("主机类型")
    private String hostType;

    /**
     * 综合负载
     */
    @ApiModelProperty("综合负载")
    private String load;

    /**
     * 操作系统类型
     */
    @ApiModelProperty("操作系统类型")
    private String osType;

    /**
     * 操心系统发行版本
     */
    @ApiModelProperty("操心系统发行版本")
    private String osVersion;

    /**
     * 主机CPU型号
     */
    @ApiModelProperty("主机CPU型号")
    private String cpuType;

    /**
     * CPU核心数
     */
    @ApiModelProperty("CPU核心数")
    private Integer numCores;

    /**
     * 线程数
     */
    @ApiModelProperty("线程数")
    private Integer numThreads;

    /**
     * 主频
     */
    @ApiModelProperty("主频")
    private String mainFrequency;

    /**
     * 硬盘名称
     */
    @ApiModelProperty("硬盘名称")
    private String diskName;

    /**
     * 硬盘型号
     */
    @ApiModelProperty("硬盘型号")
    private String diskType;

    /**
     * 硬盘容量
     */
    @ApiModelProperty("硬盘容量")
    private String diskCapacity;

    /**
     *  内存序列号
     */
    @ApiModelProperty("内存序列号")
    private String memorySerialNumber;

    /**
     * 内存生产厂家
     */
    @ApiModelProperty("内存生产厂家")
    private String memoryManufacturer;

    /**
     * 内存容量
     */
    @ApiModelProperty("内存容量")
    private String memoryCapacity;

    /**
     * 内存主频
     */
    @ApiModelProperty("内存主频")
    private String memoryFrequency;

    /**
     * 物理网卡名称
     */
    @ApiModelProperty("物理网卡名称")
    private String physicalInterfaceName;

    /**
     * IP地址信息
     */
    @ApiModelProperty("IP地址信息")
    private String ipAddress;

    /**
     * MAC地址信息
     */
    @ApiModelProperty("MAC地址信息")
    private String macAddress;

}
