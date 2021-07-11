package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ImageMetaData implements Serializable {

    
    @ApiModelProperty("磁盘控制器类型")
    private String hwDiskBus;

    
    @ApiModelProperty("虚拟网卡型号")
    private String hwVifModel;

    
    @ApiModelProperty("视频图像驱动")
    private String hwVideoModel;

    @ApiModelProperty("环境变量字符串格式")
    private String environmentStr;

    @ApiModelProperty("环境变量k-v格式")
    private Map<String, Object> environment;

    @ApiModelProperty("容器interactive参数")
    private Boolean interactive;

    @ApiModelProperty("容器tty参数")
    private Boolean tty;

    public void setEnvironmentStr(String environmentStr) {
        if (StringUtils.isNotEmpty(environmentStr)) {
            this.environmentStr = environmentStr;
            this.environment = new HashMap<>();
            if (StringUtils.isNotEmpty(environmentStr)) {
                String[] kvs = StringUtils.split(this.environmentStr, ";");
                for (String kv : kvs) {
                    String[] kvSplit = StringUtils.split(kv, "=");
                    if (kvSplit.length == 2) {
                        this.environment.put(kvSplit[0], kvSplit[1]);
                    }
                }
            }
        }
    }

    public void setEnvironment(Map<String, Object> environment) {
        if (StringUtils.isEmpty(this.environmentStr)) {
            this.environment = environment;
            this.environmentStr = StringUtils.EMPTY;
            if (this.environment != null) {
                for (var e : this.environment.entrySet()) {
                    this.environmentStr += String.join(e.getKey(), e.getValue().toString());
                }
            }
        }
    }

}
