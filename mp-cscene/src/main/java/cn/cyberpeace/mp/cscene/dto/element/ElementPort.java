package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class ElementPort {

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("name")
    private String name;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("接入模式")
    private List<AccessMode> accessModes;

    @ApiModelProperty("cloud接入模式")
    private List<CloudAccessMode> cloudAccessModes;

}
