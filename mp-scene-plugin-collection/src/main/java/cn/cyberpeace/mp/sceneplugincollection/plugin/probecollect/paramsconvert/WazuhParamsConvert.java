package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import java.util.List;

/**
 * @ClassName WazuhParamsConvert
 * @Description
 * @Author hewei
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface WazuhParamsConvert <T>{
    /**
     * convertParams
     *
     * @return
     */
    public List<T> convertParams();
}
