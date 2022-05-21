package cn.cyberpeace.mp.situationaware.model.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 通用探针Dto
 * 参数比较少
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/25 09:41
 */
@Data
public class ColTempCommonProbeDto {

    /**
     * 探针ID
     */
    @ApiModelProperty(value = "探针ID")
    @NotBlank
    private String probeId;

    /**
     * 探针名称
     */
    @ApiModelProperty(value = "探针名称")
    private String name;


    @ApiModelProperty(value = "采集模板ID")
    private String templateId;

    /**
     * 0:启用，1:禁用
     */
    @ApiModelProperty(value = "0:启用，1:禁用")
    @NotNull
    private Integer disabled;

    /**
     * 探针配置，json串，数组
     */
    @ApiModelProperty(value = "探针配置，路径数组 如：[\"/var/log/mp.log\", \"/etc/bin\"] 或者注册表选项 [\"HKEY_LOCAL_MACHINE\\SYSTEM\\Setup\"]")
    private List<String> paths;

}
