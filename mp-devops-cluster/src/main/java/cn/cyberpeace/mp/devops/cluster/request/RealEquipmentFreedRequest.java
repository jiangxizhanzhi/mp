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
public class RealEquipmentFreedRequest extends BaseRequest{

    /**
     * ID
     */
    private String uuid;


}
