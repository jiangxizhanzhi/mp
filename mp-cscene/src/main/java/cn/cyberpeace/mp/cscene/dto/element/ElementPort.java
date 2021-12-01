package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/30 14:53
 */
@Data
public class ElementPort {

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("name")
    private String name;

    @ApiModelProperty("接入模式")
    private List<AccessMode> accessModes;

}
