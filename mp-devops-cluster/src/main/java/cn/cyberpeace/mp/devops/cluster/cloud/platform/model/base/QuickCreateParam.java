package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuickCreateParam {

    private String installerUuid;

    private List<StartUpParameter> settings;

    private String name;

    private String description;

    private String logo;

    private String file;
}
