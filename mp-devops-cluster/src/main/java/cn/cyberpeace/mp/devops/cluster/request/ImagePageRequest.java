package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.RoleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 镜像分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "镜像分页请求信息")
public class ImagePageRequest extends BasePageRequest {

    /**
     * 角色
     */
    private RoleEnum role;

    /**
     * 系统类型
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "vm,container,entity,simple")
    private ImageTypeEnum imageType;

    /**
     * 镜像名称
     */
    private String name;

    public HumanCondition wrapperHumanCondition(){
        HumanCondition outerCondition = new HumanCondition();
        if (Objects.nonNull(this.imageType)) {
            outerCondition.filterEqual("image_type", this.imageType.toValue());
        }
        if (StringUtils.isNotBlank(this.name)) {
            outerCondition.filterContains("name", this.name);
        }
        if (Objects.nonNull(this.role)) {
            outerCondition.filterEqual("role", this.role.toValue());
        }
        if (Objects.nonNull(this.mainCategory)) {
            outerCondition.filterEqual("main_category", this.mainCategory);
        }
        if (Objects.nonNull(this.subCategory)) {
            outerCondition.filterEqual("sub_category", this.subCategory);
        }
        outerCondition.filterEqual("is_public", PublicEnum.ALL_PUBLIC.toValue());
        outerCondition.pagination(this.getPage(), this.getPerPage());
        outerCondition.orderBy("create_time");
        return outerCondition;
    }
}
