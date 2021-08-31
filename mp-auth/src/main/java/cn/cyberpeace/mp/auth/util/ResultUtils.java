package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.BeanUtils;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public class ResultUtils {

    @Deprecated
    public static ServiceResult commonResult(boolean success, Object source) {
        if (success) {
            if (source != null) {
                return ServiceResult.builder().source(source).build().asResult();
            }
            return ServiceResult.builder().build().asResult();
        }
        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }


    public static ListObjServiceResult objList2ListObjServiceResult(List<Objects> list, Integer total) {
        return new ListObjServiceResult(new ListObjResult(list, total).asResult()).asResult();
    }


    public static ListObjServiceResult pageInfo2ObjServiceResult(PageInfo pageInfo) {
        return new ListObjServiceResult(new ListObjResult(pageInfo.getList(), Long.valueOf(pageInfo.getTotal()).intValue()).asResult()).asResult();
    }

    public static ObjServiceResult obj2ObjServiceResult(Object obj) {
        return new ObjServiceResult(new ObjResult(obj)).asResult();
    }



    public static void throwExceptionIfError(ObjServiceResult result, String errorMsg) {
        if (StringUtils.isBlank(errorMsg)) {
            errorMsg = "execute error!";
        }
        if (Objects.isNull(result.getSource())) {
            throw new MpRuntimeException(errorMsg);
        }
    }



}
