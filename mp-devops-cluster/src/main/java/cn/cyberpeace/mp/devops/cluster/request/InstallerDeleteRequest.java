package cn.cyberpeace.mp.devops.cluster.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstallerDeleteRequest extends BaseRequest{

    /**
     * ID
     */
    @NotEmpty
    private List<String> uuids;

}
