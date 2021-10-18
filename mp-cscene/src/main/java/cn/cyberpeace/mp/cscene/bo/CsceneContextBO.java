package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.base.context.SysContextUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CsceneContextBO {

    @ApiModelProperty(value = "场景访问用户id")
    private String accessUserId;

    @ApiModelProperty(value = "透明上下文")
    private Map<String, Object> context;

    public void loadTransparentContext() {
        if (context != null) {
            SysContextUtils.setPassThroughMap(context);
        }
    }
}
