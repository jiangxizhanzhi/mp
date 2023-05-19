package cn.cyberpeace.mp.cscene.dto.protocolcollection;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibFileResult;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    MibFileInfo parentInfo;

    @ApiModelProperty(value = "下级组织")
    @JsonManagedReference
    List<MibFileInfo> children;
}
