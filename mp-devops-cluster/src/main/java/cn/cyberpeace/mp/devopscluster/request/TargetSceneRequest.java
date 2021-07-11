package cn.cyberpeace.mp.devopscluster.request;


import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetSceneRequest extends BasePageRequest implements Serializable {

    
    @ApiModelProperty(value = "系统一级类型")
    private Integer category;

    
    @ApiModelProperty(value = "系统二级类型")
    private Integer secondCategory;

    
    @ApiModelProperty(value = "系统二级类型集合")
    private List<Integer> secondCategories;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "名称集合")
    private List<String> nameList;
    
    
    @ApiModelProperty(value = "角色")
    private Integer role;

    
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty("镜像类型")
    private ImageTypeEnum imageType;

    @ApiModelProperty("忽略用户Token")
    private Boolean ignoreUser;

    @ApiModelProperty("靶标分类，1：内置靶标，2：实体靶标，3：自定义靶标")
    private Integer targetCategory;

    
    @ApiModelProperty(value = "快速构建")
    private Boolean quickCreateSupport;

    
    @ApiModelProperty(value = "系统类型")
    private Integer systemType;

    
    @ApiModelProperty(value = "系统架构")
    private String systemArchitecure;

    
    @ApiModelProperty(value = "语言")
    private String language;

    
    @ApiModelProperty(value = "镜像状态,2:已保存")
    private Integer imageStatus;

    
    @ApiModelProperty(value = "是否支持基础镜像")
    private Boolean baseImageSupport;

    @ApiModelProperty("是否模糊查询")
    private Boolean likeName = Boolean.TRUE;

    @ApiModelProperty("是否查询隐藏数据，false:排除隐藏数据")
    private Boolean isHide = Boolean.FALSE;

    
    @ApiModelProperty(value = "系统二级类型列表")
    private List<Integer> secondCategoryList;

    
    @ApiModelProperty(value = "系统类型集合：vm,container,simple,entity集合")
    private List<String> imageTypeList;

    
    @ApiModelProperty(value = "预置类型：0 自定义，1 内置")
    private List<Integer> preTypeList;

    
    @ApiModelProperty(value = "操作系统类型：1 WINDOWS, 2 LINUX, 3 UNIX, 4 BSD, 5 OTHER")
    private Integer parentSystemType;

}
