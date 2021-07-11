package cn.cyberpeace.mp.cscene.dto.protocolcollection;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibFileResult;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class MibFileInfo extends EntityNodeMibFileResult {

    @ApiModelProperty(value = "下级组织")
    List<MibFileInfo> children;
}
