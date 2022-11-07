package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/5
 */
@Getter
@Setter
public class IxiaPhaseData implements HttpData {
    private String rateDistunit;
    private String sessionsmaxPerSecond;
    private String sessionsmax;
    private String rateDistscope;
    private String rateDisttype;
    private String rampDiststeadyBehavior;
    private String rateDistmin;

    @Override
    public String toContent() {
        String content = StringUtils.replace(JsonUtils.stringify(this), "rateDist", "rateDist.");
        String content1 = StringUtils.replace(content, "sessions", "sessions.");
        return StringUtils.replace(content1, "rampDist", "rampDist.");
    }
}
