package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.analysis.function.Add;

import java.util.List;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/31
 */
@Data
public class AddSuperFlowData implements HttpData {
    private List<AddSuperFlowInfo> add;

    @Getter
    @Setter
    public static class AddSuperFlowInfo {
        private String superflow;
        private Integer weight;
    }
}
