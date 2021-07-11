package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class NetElementParseResult {
    private String id;
    private String ip;
    private List<AclsInfo> acls;
}