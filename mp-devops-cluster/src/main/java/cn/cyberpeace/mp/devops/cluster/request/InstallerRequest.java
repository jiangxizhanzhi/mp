package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "服务管理的新增请求参数")
public class InstallerRequest extends BaseRequest {

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
     * 分类
     */
    @ApiModelProperty(value = "分类")
    private List<String> categoryList;

    /**
     * 服务类型
     */
    @ApiModelProperty(value = "服务类型")
    private String type;

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表")
    private List<Resource> resources;

    /**
     * docker文件地址
     */
    @ApiModelProperty(value = "docker文件地址")
    private String docker;

    /**
     * 启动参数
     */
    @ApiModelProperty(value = "启动参数")
    private List<StartUpParameter> startUpParameters;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class Resource{

        /**
         * 平台
         */
        @ApiModelProperty(value = "平台")
        private String platform;

        /**
         * 文件
         */
        @ApiModelProperty(value = "文件")
        private String file;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class StartUpParameter{

        /**
         * 参数类型：环境变量/命令参数/端口映射
         */
        private String type;

        /**
         * 可选还是必选
         */
        private String option;

        /**
         * 参数名称
         */
        private String name;

        /**
         * 默认值
         */
        private String defaultVal;

        /**
         * 命令
         */
        private String command;

        /**
         * 容器端口
         */
        private String containerPort;

        /**
         * 主机端口
         */
        private String hostPort;

        /**
         * 描述
         */
        private String description;
    }



}
