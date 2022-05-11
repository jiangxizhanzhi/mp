package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlatformPageData<T> {

    private List<T> list;

    private Integer total;
}
