package cn.cyberpeace.mp.devopscluster.converter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageConvertModel {

    
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private boolean success;

    
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private Integer total = 0;

    
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String message;

    
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String messageZh;

    
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String errorMessage;

    
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String msg;

    
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String msgZh;
}
