package cn.cyberpeace.mp.cscene.dto.protocolcollection;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibFileResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.dto.protocolcollection
 * @Author: hhuang
 * @CreateTime: 2023/05/15  09:44
 * @Description: mib信息
 * @Version: 1.0
 */
@Data
public class MibFileInfo extends EntityNodeMibFileResult {


    @ApiModelProperty(value = "上级组织")
    MibFileInfo parentInfo;

    @ApiModelProperty(value = "下级组织")
    List<MibFileInfo> children;
}
