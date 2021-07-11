package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Data
@ApiModel("扫描")
public class Scan {

    @ApiModelProperty(value = "扫描ID")
    private Long id;

    @ApiModelProperty(value = "扫描时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime time;

}
