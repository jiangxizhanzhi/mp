package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 */
@Getter
@Setter
public class NetElementParseResult {
    private String id;
    private String ip;
    private List<AclsInfo> acls;
}