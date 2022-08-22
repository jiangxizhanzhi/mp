package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devopscluster.request.BasePageRequest;

public interface BaseConverter {

    default String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    default String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getOffset,
                BasePageRequest::getPage,BasePageRequest::getPerPage);
    }
}
