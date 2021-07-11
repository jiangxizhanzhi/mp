package cn.cyberpeace.mp.frontbusiness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlAcCtlGroupBo {

    public static final String QUERY_FIELD_GROUP = "group";
    public static final String QUERY_FIELD_URL = "url";
    public static final String QUERY_FIELD_IS_PATTERN = "isPattern";
    public static final String QUERY_FIELD_METHODS= "methods";
    public static final String QUERY_FIELD_TARGETS = "targets";

    private String group;

    private String targets;

    private String url;

    private Integer isPattern;

    private List<String> methods;

}
