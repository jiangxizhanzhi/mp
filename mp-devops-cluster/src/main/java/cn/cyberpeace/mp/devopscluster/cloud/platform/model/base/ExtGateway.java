package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("网关信息")
public class ExtGateway implements Serializable {

    private List<PortConfig> portConfigs;

    
    private AccessProtocol consoleConfig;

    public AccessProtocol getConsoleConfig() {
        return Objects.nonNull(consoleConfig) && StringUtils.isBlank(consoleConfig.getProtocol()) ?  null : consoleConfig;
    }
}
