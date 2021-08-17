package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InstallerCloudPlatformModel extends BaseCloudPlatformModel {

    private List<Installer> items;
}
