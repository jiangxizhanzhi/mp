package cn.cyberpeace.mp.frontbusiness.request.urlac;

import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlAccessCtlRequest {

    @NotBlank(groups = {UpdateGroup.class})
    @ApiModelProperty(value = "ID")
    private String id;

    @NotBlank(groups = AddGroup.class)
    @ApiModelProperty(value = "请求url")
    private String url;

    @NotEmpty(groups = AddGroup.class)
    @ApiModelProperty(value = "请求方法")
    private List<String> methods;

    @NotEmpty(groups = AddGroup.class)
    @ApiModelProperty(value = "权限列表")
    private List<String> targetIds;

}
