package cn.cyberpeace.mp.cscene.service.exportfile;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
@Getter
public class PageIterator<D, R extends QueryParams> implements Iterator<List<D>> {

    private final R originRequest;
    private final Function<R, ListObjServiceResult<D>> pageDataService;

    private ListObjServiceResult<D> lastResult;

    private final Class<D> dataClass;


    public PageIterator(R originRequest, Function<R, ListObjServiceResult<D>> pageDataService) {
        this.originRequest = originRequest;
        originRequest.setPerPage(1000);
        originRequest.setPage(1);
        this.pageDataService = pageDataService;
        this.lastResult = pageDataService.apply(originRequest);
        this.dataClass = initDataClass();
    }

    @SuppressWarnings("unchecked")
    private Class<D> initDataClass() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) superclass;
            Type[] actualTypes = pt.getActualTypeArguments();
            return (Class<D>) actualTypes[0]; // 第一个泛型参数是D
        } else {
            throw new IllegalStateException("泛型类型未指定");
        }
    }





    @Override
    public boolean hasNext() {
        if (!lastResult.getData().getList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<D> next() {
        if (hasNext()) {
            ListObjServiceResult<D> result = lastResult;
            originRequest.setPage(originRequest.getPage() + 1);
            lastResult = pageDataService.apply(originRequest);
            return result.getData().getList();
        } else {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("没有下一个元素了"));
        }
    }
}
