package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.dto.InstallerDTO;
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
public class ImagesBuildRequest extends BaseRequest {


    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "镜像名称")
    private String imageName;

    
    @ApiModelProperty(value = "服务集合")
    private List<InstallerDTO> installerDTOList;


}
