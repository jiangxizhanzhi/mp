package cn.cyberpeace.mp.frontbusiness.dto;

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

    public static final String QUERY_FIELD_METHODS = "methods";
    public static final String QUERY_FIELD_GROUPS = "groups";
    public static final String QUERY_FIELD_TARGET_IDS = "targetIds";

    private String url;

    private Integer isPattern;

    @JsonIgnore
    private String methodStr;

    private List<String> methods;

    @JsonIgnore
    private String targetsStr;

    private List<String> targetIds;

    @JsonIgnore
    private String groupsStr;

    private List<String> groups;
}
