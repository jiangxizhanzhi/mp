package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 镜像新增请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageByUuidsResponse extends BaseRequest {

    /**
     * UUID
     */
    private List<String> uuids;

}
