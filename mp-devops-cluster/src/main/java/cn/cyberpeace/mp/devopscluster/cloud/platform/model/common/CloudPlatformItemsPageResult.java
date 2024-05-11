package cn.cyberpeace.mp.devopscluster.cloud.platform.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页
 * @author wanggang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlatformItemsPageResult<T> extends BaseCloudPlatformResult {

    private CloudPlatformItemData<T> data;

    private List<?> items;

    private Integer total;







    @Data
    public static class CloudPlatformItemData<T> {
        private Integer total;
        private List<T> items;

        private List<T> list;

    }
}