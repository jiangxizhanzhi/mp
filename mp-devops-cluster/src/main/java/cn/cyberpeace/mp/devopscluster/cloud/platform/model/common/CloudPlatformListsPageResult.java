package cn.cyberpeace.mp.devopscluster.cloud.platform.model.common;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CloudPlatformListsPageResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
public class CloudPlatformListsPageResult<T> extends BaseCloudPlatformResult {

    private CloudPlatformListData<T> data;




    @Data
    public static class CloudPlatformListData<T> {
        private Integer total;
        private List<T> list;
    }
}
