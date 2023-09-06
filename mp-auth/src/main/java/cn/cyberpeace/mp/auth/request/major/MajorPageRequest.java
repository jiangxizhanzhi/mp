package cn.cyberpeace.mp.auth.request.major;


import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.log.request.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 专业分页查询条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class MajorPageRequest extends BasePageRequest {

    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String majorName;


    public HumanCondition convertToHumanCondition() {
        this.ensure();
        HumanCondition humanCondition = new HumanCondition();
        if (StringUtils.hasText(this.majorName)) {
            humanCondition.filterContains(MajorModel::getMajorName, this.majorName);
        }
        humanCondition.setOffset((this.getPage() -1) * this.getPerPage());
        humanCondition.setLimit(this.getPerPage());
        humanCondition.reverseOrderBy(MajorModel::getUpdateTime).orderBy(MajorModel::getMajorName);
        return humanCondition;
    }

}
