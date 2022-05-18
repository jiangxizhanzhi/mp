package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 14:58
 */
@Data
public class ElementNetiface extends Netiface {

    @ApiModelProperty(value = "IP地址")
    private String ip;

}
