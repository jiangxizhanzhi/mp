package cn.cyberpeace.mp.situationaware.model.wazuh.common;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:08
 */
@Data
public class BaseRequest {

    private Integer limit;

    private Integer offset;

    private String select;

}
