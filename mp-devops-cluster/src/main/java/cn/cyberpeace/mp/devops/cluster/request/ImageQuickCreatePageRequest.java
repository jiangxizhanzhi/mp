package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 镜像分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "镜像分页请求信息")
public class ImageQuickCreatePageRequest extends BasePageRequest {

    /**
     * 快速创建
     */
    private Integer quickCreateSupport;

    public HumanCondition wrapperHumanCondition(){
        HumanCondition outerCondition = new HumanCondition();
        if (Objects.nonNull(this.quickCreateSupport)) {
            outerCondition.filterEqual("quick_create_support", this.quickCreateSupport);
        }
        outerCondition.pagination(this.getPage(), this.getPerPage());
        outerCondition.orderBy("create_time");
        return outerCondition;
    }
}
