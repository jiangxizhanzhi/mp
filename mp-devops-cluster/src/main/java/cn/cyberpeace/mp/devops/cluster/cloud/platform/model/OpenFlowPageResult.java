package cn.cyberpeace.mp.devops.cluster.cloud.platform.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenFlowPageResult extends BaseCloudPlatformModel {

    private List<OpenFlow> data;
}
