package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostEventQueryParams2 extends HostEventQueryParams implements HttpParam {
    private String id ;
}
