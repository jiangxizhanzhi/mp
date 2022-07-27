package cn.cyberpeace.mp.common.util;

import cn.cyberpeace.mp.common.model.ApiModel;
import cn.cyberpeace.mp.common.vo.ApiVo;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/27
 */
public class Model2VoUtil {
    public static ApiVo apiModel2Vo(ApiModel model, Locale locale) {
        String name = Objects.equals(locale, Locale.US) ? model.getEnName() : model.getChName();
        return ApiVo.builder()
                .name(name)
                .code(model.getCode())
                .build();
    }

    public static List<ApiVo> getVoList(List<? extends ApiModel> models) {
        List<ApiVo> voList = new ArrayList<>();
        Locale locale = LocaleContextHolder.getLocale();
        for (ApiModel model : models) {
            voList.add(apiModel2Vo(model, locale));
        }
        return voList;
    }
}
