package cn.cyberpeace.mp.log.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseCloudPlatformResult extends BaseModel {

    
    @ApiModelProperty(hidden = true)
    private boolean success;

    
    @ApiModelProperty(hidden = true)
    private String requestId;

    
    @ApiModelProperty(hidden = true)
    private Integer total = 0;

    
    @ApiModelProperty(hidden = true)
    private String message;

    
    @ApiModelProperty(hidden = true)
    private String code;
}
