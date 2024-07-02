package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName NameQueryParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CommonQueryParam
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameQueryParam extends CommonQueryParam {
    private String name;
}
