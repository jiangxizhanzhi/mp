package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.converter.model.SerialPortConvertModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SerialPortVO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialPortVO {

    @ApiModelProperty("串口服务ID")
    private String ccmServerId;

    @ApiModelProperty("串口服务名")
    private String ccmServerName;

    @ApiModelProperty("串口列表")
    private List<SerialPortConvertModel> children;
}
