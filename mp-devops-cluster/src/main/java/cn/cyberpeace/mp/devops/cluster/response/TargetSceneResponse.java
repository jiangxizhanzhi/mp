package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetSceneResponse implements Serializable {

    @ApiModelProperty("靶标集合")
    private List<TargetScene> list;
}
