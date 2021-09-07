package cn.cyberpeace.mp.flow.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 新增脚本请求体
 * @Author qiaokang
 * @Date：2021/8/10 15:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增脚本请求体")
public class AddScriptsRequest {

    /**
     * 资源文件名称
     */
    @ApiModelProperty("资源文件名称")
    private String name;

    /**
     * 资源描述
     */
    @ApiModelProperty("资源描述")
    private String description;

    /**
     * 类别id: 1 扫描，2爆破，3其他
     */
    @ApiModelProperty("类别id: 1 扫描，2爆破，3其他")
    private Integer categoryId = 3;

    /**
     * 标签id
     */
    @ApiModelProperty("标签id")
    private Integer tagId;

    /**
     * 资源文件的类别，1：攻击脚本，2：恶意文件
     */
    @ApiModelProperty("资源文件的类别，1：攻击脚本，2：恶意文件")
    private Integer type = 1;

    /**
     * 是否使用默认镜像: 0 否， 1 是
     */
    @ApiModelProperty("是否使用默认镜像: 0 否， 1 是")
    private int isDefault = 1;

    /**
     * 如果是攻击脚本，需要填写运行参数
     */
    @ApiModelProperty("如果是攻击脚本，需要填写运行参数")
    private String runCmd;

    /**
     * 如果是攻击脚本，需要填写如何获取返回结果的命令
     */
    @ApiModelProperty("如果是攻击脚本，需要填写如何获取返回结果的命令")
    private String getResultCmd;

    @JsonProperty(value = "is_default")
    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        isDefault = isDefault;
    }
}
