package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 扩展镜像附加信息
 * @author zhangjiaxin
 * @date 2022年11月8日下午1:42:54 
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("扩展镜像附加信息")
public class AdditionalInfo {

    @ApiModelProperty("漏洞信息文件路径")
    private String vulnerabilityInfo;
    
    @ApiModelProperty("服务信息")
    private List<ServiceInfo> serviceInfos;
    
    @ApiModelProperty("依赖服务")
    private List<DependentService> dependentServices;
    
    @ApiModelProperty("软件信息")
    private List<SoftwareInfo> softwareInfos;
    
    @ApiModelProperty("源码信息")
    private List<SourceInfo> sourceInfos;
    
    @ApiModelProperty("密码信息")
    private List<PasswordInfo> passwdInfos;
    
    @Data
    public class ServiceInfo {
        private String cat;
        private String name;
        private Integer port;
        private Boolean hasVulnerability;
        private String desc;
    }
    
    @Data
    public class DependentService extends ServiceInfo {
        private String dependentIp;
    }
    
    @Data
    public class SoftwareInfo {
        private String cat;
        private String name;
        private String language;
        private Boolean hasVulnerability;
        private String desc;
        private String version;
    }
    
    @Data
    public class SourceInfo extends SoftwareInfo {
        
    }
    
    @Data
    public class PasswordInfo {
        private String user;
        private String passwd;
        private String relatedService;
        private String desc;
    }
    
}
