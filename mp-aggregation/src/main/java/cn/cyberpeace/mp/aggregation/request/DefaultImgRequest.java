package cn.cyberpeace.mp.aggregation.request;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassName DefaultImgRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultImgRequest {

    @NotNull
    private DefaultImgDirEnum defaultImgDirEnum;
}
