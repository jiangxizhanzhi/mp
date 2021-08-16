package cn.cyberpeace.mp.devops.cluster.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "实装新增请求信息")
public class RealEquipmentAddRequest extends BaseRequest{

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String imgIconUrl;

    /**
     * 型号
     */
    @ApiModelProperty(value = "型号")
    private String type;

    /**
     * 参数
     */
    @ApiModelProperty(value = "参数")
    private String parameter;

    /**
     * 角色
     */
    @ApiModelProperty(value = "角色")
    private String role;

    /**
     * 主分类
     */
    @ApiModelProperty(value = "主分类")
    private String mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty(value = "子分类")
    private String subCategory;

    /**
     * 实体类型
     */
    @ApiModelProperty(value = "实体类型")
    private String entityType;

    /**
     * 实体类型明细
     */
    @ApiModelProperty(value = "实体类型明细")
    private List<EntityTypeDetail> typeDetailList;

    /**
     * 系统信息
     */
    @ApiModelProperty(value = "系统信息")
    private SystemInfo systemInfo;

    /**
     * 接入方式
     */
    @ApiModelProperty(value = "接入方式")
    private List<AccessWay> accessWays;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class EntityTypeDetail{

        /**
         * 名称
         */
        @ApiModelProperty(value = "名称")
        private String name;

        /**
         * 端口
         */
        @ApiModelProperty(value = "端口")
        private String port;
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
