package cn.cyberpeace.mp.aggregation.request;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultImgRequest {

    @NotNull
    private DefaultImgDirEnum defaultImgDirEnum;
}
