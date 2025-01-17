package cn.cyberpeace.mp.aggregation.request;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 默认img请求
 *
 * @author wanggang@date 2025/01/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultImgRequest {

    @NotNull
    private DefaultImgDirEnum defaultImgDirEnum;
}
