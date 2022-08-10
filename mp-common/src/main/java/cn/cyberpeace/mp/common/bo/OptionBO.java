package cn.cyberpeace.mp.common.bo;

import cn.cyberpeace.mp.common.model.OptionModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionBO<T> {
    private T code;
    private String name;
    private List<OptionBO<T>> children;

    public void fromModel(OptionModel model, Locale locale, Function<String, T> function) {
        String boName = Objects.equals(locale, Locale.US) ? model.getEnName() : model.getZhName();
        this.setName(boName);
        this.setCode(function.apply(model.getCode()));
    }
}
