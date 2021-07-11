package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;



@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagePageRequest extends BasePageRequest {

    
    @ApiModelProperty(value = "角色")
    private Integer role;

    
    @ApiModelProperty(value = "一级分类")
    private Integer mainCategory;

    
    @ApiModelProperty(value = "二级分类")
    private Integer subCategory;

    
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty(value = "镜像类型")
    private String imageType;

    
    @ApiModelProperty(value = "镜像名称")
    private String name;

    @ApiModelProperty("是否模糊匹配镜像名称,默认：true")
    private Boolean likeName = Boolean.TRUE;

    
    @ApiModelProperty(value = "快速构建")
    private Boolean quickCreateSupport;

    
    @ApiModelProperty(value = "系统类型")
    private Integer systemType;

    
    @ApiModelProperty(value = "系统架构")
    private String systemArchitecure;

    
    @ApiModelProperty(value = "语言")
    private String language;

    
    @ApiModelProperty(value = "镜像状态")
    private Integer imageStatus;

    
    @ApiModelProperty(value = "是否支持基础镜像")
    private Boolean baseImageSupport;

    @ApiModelProperty("已启动镜像")
    private Boolean vmStarted;

    @ApiModelProperty("靶标创建用户名称")
    private String createUserName;

    
    @ApiModelProperty(value = "排序字段")
    private String orders;

    public Boolean getLikeName() {
        return Objects.isNull(likeName) ? Boolean.TRUE : likeName;
    }
}
