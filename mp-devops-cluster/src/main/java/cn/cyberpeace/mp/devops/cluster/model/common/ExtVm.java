package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtVm {

    private String flavor;

    private Boolean initSupport;

    private Boolean quickCreateSupport;

    private String quickCreateParams;

    private String accessProtocols;

    private String extInfo;
}
