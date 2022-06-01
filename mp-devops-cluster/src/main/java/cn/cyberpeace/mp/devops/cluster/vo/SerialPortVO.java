package cn.cyberpeace.mp.devops.cluster.vo;

import cn.cyberpeace.mp.devops.cluster.converter.model.SerialPortConvertModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialPortVO {

    private String ccmServerId;

    private String ccmServerName;

    private List<SerialPortConvertModel> children;
}
