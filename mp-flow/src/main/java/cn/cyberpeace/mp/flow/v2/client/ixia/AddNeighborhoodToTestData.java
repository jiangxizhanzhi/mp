package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/11/1
 */
@Getter
@Setter
public class AddNeighborhoodToTestData implements HttpData {
    private String neighborhood;

    @Override
    public String toContent() {
        return StringUtils.replace(JsonUtils.stringify(this), "neighborhood", "");
    }
}
