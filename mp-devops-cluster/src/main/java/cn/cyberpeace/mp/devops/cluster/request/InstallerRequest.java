package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.model.common.Resource;
import cn.cyberpeace.mp.devops.cluster.model.common.StartUpParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "服务管理的新增请求参数")
public class InstallerRequest extends BaseRequest {

    /**
     * UUID
     */
    @ApiModelProperty("uuid")
    private String uuid;

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
     * logo文件流
     */
    @ApiModelProperty("logo文件流")
    private MultipartFile logoFile;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String logo;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类")
    private List<String> categories;

    /**
     * 服务类型
     */
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

    @ApiModelProperty("docker参数")
    private DockerInstallerData dockerInstallerData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DockerInstallerData {

        /**
         * 启动参数（包装对象）
         */
        @ApiModelProperty(value = "启动参数")
        private List<StartUpParameter> startUpParameterList;

        @ApiModelProperty("docker文件流")
        private MultipartFile dockerFile;
    }


    @ApiModelProperty("windows服务资源流")
    private List<MultipartFile> multipartFiles;

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表")
    private List<Resource> resourceList;


}
