package cn.cyberpeace.mp.devops.cluster.request;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "实装分页请求信息")
public class RealEquipmentAddRequest extends BaseRequest{

    /**
     * 系统类型
     */
    private Integer systemType;

    /**
     * 名称
     */
    private String name;
}
