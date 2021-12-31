package cn.cyberpeace.mp.devops.cluster.request;


import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtEntityAccess;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtSystemInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RealEquipmentRequest extends BaseRequest{

    /**
     * uuid
     */
    @ApiModelProperty("uuid")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * logo文件流
     */
    @ApiModelProperty(value = "logo文件流",hidden = true)
    private MultipartFile logoFile;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String logo;

    /**
     * 角色
     */
    @ApiModelProperty("角色")
    private Integer role;

    /**
     * 分类
     */
    @ApiModelProperty("一级分类")
    private Integer mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty("二级分类")
    private Integer subCategory;

    /**
     * 子分类
     */
    @ApiModelProperty("三级分类")
    private Integer thirdCategory;

    /**
     * 系统信息
     */
    @ApiModelProperty("系统信息")
    private ExtSystemInfo systemInfo;

    /**
     * 实体信息
     */
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    /**
     * 交换机
     */
    @ApiModelProperty("交换机接入配置")
    private List<ExtEntityAccess> extEntityAccess;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;
}
