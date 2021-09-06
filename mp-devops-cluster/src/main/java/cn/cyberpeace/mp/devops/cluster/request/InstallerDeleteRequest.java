package cn.cyberpeace.mp.devops.cluster.request;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "服务批量删除请求信息")
public class InstallerDeleteRequest extends BaseRequest{

    /**
     * ID
     */
    @NotEmpty
    private List<String> uuids;


}
