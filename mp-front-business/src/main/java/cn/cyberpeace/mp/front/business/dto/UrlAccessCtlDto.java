package cn.cyberpeace.mp.front.business.dto;

import cn.cyberpeace.mp.auth.model.TargetModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class UrlAccessCtlDto {

    private String id;

    private String url;

    private Integer isPattern;

    @JsonIgnore
    private String methodStr;

    private List<String> methods;

    @JsonIgnore
    private String targetsStr;

    private List<String> targetIds;
}
