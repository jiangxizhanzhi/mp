package cn.cyberpeace.mp.situationaware.model.dto.probe;

import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/20 09:26
 */
@Data
@Builder
public class ProbeTypeVO {

    private String name;

    private int code;

}
