package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.paramsconvert;

import java.util.List;

/**
 * @Description: 探针参数转换接口
 * @Author hewei
 * @Date：2022/5/6 9:23
 */
public interface WazuhParamsConvert <T>{
    public List<T> convertParams();
}
