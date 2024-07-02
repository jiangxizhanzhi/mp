package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName QuickCreatePageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class QuickCreatePageRequest {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "镜像类型")
    private String imageType;
    @ApiModelProperty(value = "系统三级分类")
    private String systemType;
    @ApiModelProperty(value = "规格")
    private String flavor;
    @ApiModelProperty(value = "分页页码")
    private Integer page;
    @ApiModelProperty(value = "分页大小")
    private Integer perPage;
}
