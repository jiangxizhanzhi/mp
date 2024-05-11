package cn.cyberpeace.mp.devopscluster.cloud.platform.model.common;

import lombok.Data;

import java.util.List;

@Data
public class CloudPlatformListsPageResult<T> extends BaseCloudPlatformResult {

    private CloudPlatformListData<T> data;




    @Data
    public static class CloudPlatformListData<T> {
        private Integer total;
        private List<T> list;
    }
}
