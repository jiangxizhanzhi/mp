package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/25
 */
@Getter
@Setter
public class StartInfoData implements HttpData {
    private String id;
    private Integer action;
    private LocalDateTime time;
    private Integer duration;
    private Integer bandwidth;
    private String targetIp;
    List<Flow> flows = new ArrayList<>();
    List<TrexClient.LoadInfo> loadLists = new ArrayList<>();

    @Getter
    @Setter
    public static class Flow {
        private String flowId;
        private Integer weight;
        private Integer resourceType;
        private Integer delay;
    }

    @Override
    public String toContent() {
        return JsonUtils.snakeStringify(this);
    }
}
