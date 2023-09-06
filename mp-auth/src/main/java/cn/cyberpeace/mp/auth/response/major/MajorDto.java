package cn.cyberpeace.mp.auth.response.major;

import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.base.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * 专业信息dto
 */
@Data
@Slf4j
public class MajorDto extends BaseModel {

    @ApiModelProperty("专业名称")
    private String majorName;

    @ApiModelProperty("专业描述")
    private String majorDescription;


    public static MajorDto convertModelToDto(MajorModel majorModel) {
        MajorDto majorDto = new MajorDto();
        BeanUtils.copyProperties(majorModel, majorDto);
        return majorDto;
    }

}
