package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchConvertModel {

    @ApiModelProperty(value = "标签名")
    private String label;

    @ApiModelProperty(value = "标签值")
    private String value;

    @ApiModelProperty(value = "字标签")
    private List<Children> children;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Children{

        private String label;

        private String value;
    }
}
