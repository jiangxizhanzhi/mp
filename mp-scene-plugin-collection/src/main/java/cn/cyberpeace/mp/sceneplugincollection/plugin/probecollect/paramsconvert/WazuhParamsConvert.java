package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import java.util.List;


public interface WazuhParamsConvert <T>{
    public List<T> convertParams();
}
