package cn.cyberpeace.mp.devopscluster.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RealEquipmentDeleteRequest extends BaseRequest{

    /**
     * ID
     */
    private List<String> uuids;


}
