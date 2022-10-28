package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/26
 */
@Getter
@Setter
public class TaskLastStatusResponseData implements HttpData {
    private List<singleTask> rows;

    @Getter
    @Setter
    public static class singleTask {
        private String id;
        private String taskUuid;
        private Integer groupCount;
        private Integer tagId;
        private String portName;
        private String flowId;
        private Integer second;
        private Integer cpsSuccess;
        private Integer cpsTotal;
        private Long opackets;
        private Integer obytes;
        private Double txBps;
        private Integer oerrors;
        private Integer status;
        private String recordTime;
        private String createTime;
    }
}
