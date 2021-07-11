package cn.cyberpeace.mp.devopscluster.request;


import io.swagger.annotations.ApiModelProperty;
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
public class InstallerDeleteRequest extends BaseRequest{

    
    @NotEmpty
    @ApiModelProperty(value = "UUIDS")
    private List<String> uuids;

}
