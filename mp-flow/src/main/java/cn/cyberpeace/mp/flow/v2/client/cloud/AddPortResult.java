package cn.cyberpeace.mp.flow.v2.client.cloud;

import lombok.Getter;
import lombok.Setter;
import springfox.documentation.swagger2.mappers.LicenseMapper;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/24
 */
@Getter
@Setter
public class AddPortResult {
    private String fixedIp;
    private String macAddress;
}
