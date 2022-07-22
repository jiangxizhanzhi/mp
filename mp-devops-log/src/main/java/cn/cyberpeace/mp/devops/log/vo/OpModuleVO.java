package cn.cyberpeace.mp.devops.log.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/20
 */
@Data
@Builder
public class OpModuleVO {

    private Integer code;
    private String name;
}
