package cn.cyberpeace.mp.cscene.dto.protocolcollection;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeMibFileResult;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName MibFileInfo
 * @Description mib信息
 * @Author hhuang
 * @Date 2024/07/02
 * @Version 1.0
 * @see EntityNodeMibFileResult
 */
@Data
public class MibFileInfo extends EntityNodeMibFileResult {

    @ApiModelProperty(value = "下级组织")
    List<MibFileInfo> children;
}
