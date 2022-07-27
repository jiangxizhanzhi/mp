package cn.cyberpeace.mp.common.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/27
 */
@Data
@Builder
public class ApiVo {
    private Integer code;
    private String name;
}
