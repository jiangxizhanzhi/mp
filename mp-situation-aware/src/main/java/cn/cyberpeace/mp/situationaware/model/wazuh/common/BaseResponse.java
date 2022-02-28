package cn.cyberpeace.mp.situationaware.model.wazuh.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 09:37
 */
@Data
public class BaseResponse<T> implements Serializable {

    private Data<T> data;

    private String message;

    private Integer error;


    @lombok.Data
    public static class Data<T> {

        @JSONField(name = "affected_items")
        private List<T> affectedItems;

        @JSONField(name = "failed_items")
        private List<T> failedItems;

        @JSONField(name = "total_affected_items")
        private Integer totalAffectedItems;

        @JSONField(name = "total_failed_items")
        private Integer totalFailedItems;
    }

}
