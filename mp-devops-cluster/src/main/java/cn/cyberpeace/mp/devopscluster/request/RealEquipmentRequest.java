package cn.cyberpeace.mp.devopscluster.request;


import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.EntityUsb;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntityAccess;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
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

    
    @ApiModelProperty("uuid")
    private String uuid;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty(value = "logo文件流",hidden = true)
    private MultipartFile logoFile;

    
    @ApiModelProperty("图标")
    private String logo;

    
    @ApiModelProperty("角色")
    private Integer role;

    
    @ApiModelProperty("一级分类")
    private Integer mainCategory;

    
    @ApiModelProperty("二级分类")
    private Integer subCategory;

    
    @ApiModelProperty("三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty("系统信息")
    private ExtSystemInfo systemInfo;

    
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    
    @ApiModelProperty("实体USB信息")
    private EntityUsb extEntityUsb;

    
    @ApiModelProperty("交换机接入配置")
    private List<ExtEntityAccess> extEntityAccess;

    
    @ApiModelProperty("描述")
    private String description;
}
