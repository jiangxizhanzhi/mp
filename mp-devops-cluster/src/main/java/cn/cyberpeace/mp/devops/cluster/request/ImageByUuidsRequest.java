package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 镜像新增请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageByUuidsRequest extends BaseRequest {

    /**
     * UUID
     */
    @NotEmpty
    @ApiModelProperty("UUID集合")
    private List<String> uuids;

}
