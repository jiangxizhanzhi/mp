package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseCloudPlatformResult {

    
    @ApiModelProperty(hidden = true)
    private boolean success;

    
    @ApiModelProperty(hidden = true)
    private String requestId;

    
    @ApiModelProperty(hidden = true)
    private Integer total = 0;

    
    @ApiModelProperty(hidden = true)
    private String message;

    
    @ApiModelProperty(hidden = true)
    private String messageZh;

    
    @ApiModelProperty(hidden = true)
    private String errorMessage;

    
    @ApiModelProperty(hidden = true)
    private String code;

    
    @ApiModelProperty(hidden = true)
    private String tmpdirUuid;

    
    @ApiModelProperty(hidden = true)
    private List<String> fileNames;

    
    @ApiModelProperty(hidden = true)
    private String msg;

    
    @ApiModelProperty(hidden = true)
    private String msgZh;

    
    @ApiModelProperty(hidden = true)
    private String url;
}
