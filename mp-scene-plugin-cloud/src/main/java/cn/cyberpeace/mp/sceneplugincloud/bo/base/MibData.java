package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.bo.base
 * @Author: hhuang
 * @CreateTime: 2023/05/18  17:52
 * @Description: mib数据
 * @Version: 1.0
 */
@Data
public class MibData {

    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "oid")
    private String oid;
    @ApiModelProperty(value = "被管理对象类型的关键字")
    private String syntax;
    @ApiModelProperty(value = "节点支持的访问方式")
    private String maxAccess;
    @ApiModelProperty(value = "节点的状态")
    private String status;
    @ApiModelProperty(value = "父层级oid")
    private String parent;

    @ApiModelProperty(value = "下级数据")
    @JsonManagedReference
    private List<MibData> children;
}
