package cn.cyberpeace.mp.collection.core.request.wazuh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest {

    private Integer page;

    private Integer pageSize;

    private Integer perPage;

    private String[] select;

    /**
     * @Deprecated 排序字段
     */
    private String[] sort;


    public void ensure() {
        if (perPage != null) {
            pageSize = perPage;
        }

        // 默认值
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }
    }

}
