package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlatformPageData {

    private List<?> list = new ArrayList<>();

    private Integer total = 0;
}
