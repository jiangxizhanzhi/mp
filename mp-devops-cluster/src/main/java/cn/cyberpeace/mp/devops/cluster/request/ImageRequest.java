package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 镜像新增请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "镜像新增/编辑请求信息")
public class ImageRequest extends BaseRequest {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称",required = true)
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述",required = true)
    private String description;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标",required = true)
    private String logo;

    /**
     * 角色：网络/网关/终端
     */
    @ApiModelProperty(value = "角色：网络/网关/终端")
    private Integer role;

    /**
     * 主分类
     */
    @ApiModelProperty(value = "主分类")
    private Integer mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty(value = "子分类")
    private Integer subCategory;

    /**
     * 虚拟类型
     */
    @ApiModelProperty(value = "虚拟类型")
    private Integer virtualType;

    /**
     * 扩展的虚拟机信息
     */
    @ApiModelProperty(value = "虚拟类型")
    private ExtVm extVm;

    /**
     * 系统信息
     */
    @ApiModelProperty(value = "系统信息")
    private SystemInfo systemInfo;

    /**
     * 系统镜像
     */
    @ApiModelProperty(value = "系统镜像")
    private SystemImage systemImage;

    /**
     * 虚拟类型明细
     */
    @ApiModelProperty(value = "虚拟类型明细")
    private String virtualTypeDetail;

    /**
     * 端口信息
     */
    private List<PortsInfo> ports;

    /**
     * 接入方式
     */
    @ApiModelProperty(value = "接入方式")
    private List<AccessWay> accessWays;

    /**
     * 快速构建
     */
    @ApiModelProperty(value = "快速构建")
    private Boolean fastBuild;

    /**
     * 云初始化
     */
    @ApiModelProperty(value = "云初始化")
    private Boolean cloudInit;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class PortsInfo{

        /**
         * 端口类型
         */
        private Integer type;

        /**
         * 名称
         */
        private String name;

        /**
         * 端口网段
         */
        private String networkSegment;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class SystemInfo{

        /**
         * 操作系统
         */
        @ApiModelProperty(value = "操作系统")
        private String operationSystem;

        /**
         * 位
         */
        @ApiModelProperty(value = "位")
        private String bit;

        /**
         * 语言
         */
        @ApiModelProperty(value = "语言")
        private String language;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class SystemImage{

        /**
         * 镜像类型
         */
        @ApiModelProperty(value = "镜像类型")
        private String imageType;

        /**
         * 上传协议
         */
        @ApiModelProperty(value = "上传协议")
        private String uploadProtocol;

        /**
         * 上传方式
         */
        @ApiModelProperty(value = "上传方式")
        private String uploadWay;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class AccessWay{

        /**
         * 协议
         */
        @ApiModelProperty(value = "协议")
        private String protocol;

        /**
         * 端口
         */
        @ApiModelProperty(value = "端口")
        private String port;

        /**
         * 用户名
         */
        @ApiModelProperty(value = "用户名")
        private String userName;

        /**
         * 密码
         */
        @ApiModelProperty(value = "密码")
        private String pwd;

        /**
         * 描述
         */
        @ApiModelProperty(value = "描述")
        private String description;
    }
}
