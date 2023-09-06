package cn.cyberpeace.mp.auth.model;


import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.repository.MajorMapper;
import cn.cyberpeace.mp.auth.service.MajorService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 专业配置信息数据对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_major")
public class MajorModel extends BaseModel {

    @ApiModelProperty(value = "专业名称")
    private String majorName;

    @ApiModelProperty(value = "专业描述")
    @DefaultStr
    private String majorDescription;


    public void checkMajorNameNotDuplicate(MajorService majorService) {
        HumanCondition humanCondition = new HumanCondition().filterEqual(MajorModel::getMajorName, this.getMajorName()).select(MajorModel::getMajorName);
        if (StringUtils.hasText(this.getId())) {
            humanCondition.filterNotEqual(MajorModel::getId, this.getId());
        }
        List<MajorModel> sameNameMajors = majorService.getMapper().list(humanCondition);
        if (CollectionUtils.isEmpty(sameNameMajors)) {
            return;
        } else {
            throw new MpRuntimeException(AuthErrorCode.MAJOR_ADD_NAME_DUPLICATE_ERROR);
        }
    }
}
