package cn.cyberpeace.mp.frontbusiness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/11/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlAcCtlGroupBo {

    private String group;

    private String targets;

    private String url;

    private Integer isPattern;

    private List<String> methods;

}
