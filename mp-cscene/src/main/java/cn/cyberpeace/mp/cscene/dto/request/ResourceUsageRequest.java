package cn.cyberpeace.mp.cscene.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 资源使用请求体
 * @Author zhouxinghuai
 * @Date：2021/11/24 14:22
 */
@Data
public class ResourceUsageRequest {

    @NotNull
    private String sceneId;

    private List<String> elementIds;

}
