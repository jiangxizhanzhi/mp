package cn.cyberpeace.mp.front.business.dto;

import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 19:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlAccessCtlRequest {

    @NotBlank(groups = {UpdateGroup.class})
    private String id;

    @NotBlank(groups = AddGroup.class)
    private String url;

    @NotBlank(groups = AddGroup.class)
    private List<String> methods;

    @NotBlank(groups = AddGroup.class)
    private List<String> targetIds;

}
