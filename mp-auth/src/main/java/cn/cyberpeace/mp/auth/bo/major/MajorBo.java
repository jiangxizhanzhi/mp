package cn.cyberpeace.mp.auth.bo.major;

import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.base.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 专业实体对象
 */
@Data
public class MajorBo extends BaseModel {

    @ApiModelProperty("专业名称")
    private String majorName;

    @ApiModelProperty("专业描述")
    private String majorDescription;

    public static MajorBo convertModelToBo(MajorModel majorModel) {
        MajorBo majorBo = new MajorBo();
        BeanUtils.copyProperties(majorModel, majorBo);
        return majorBo;
    }

}
