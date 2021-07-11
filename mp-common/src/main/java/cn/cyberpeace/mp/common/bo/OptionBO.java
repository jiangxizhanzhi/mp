package cn.cyberpeace.mp.common.bo;

import cn.cyberpeace.mp.common.model.OptionModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionBO<T> {

    @ApiModelProperty(value = "编码")
    private T code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "子选项")
    private List<OptionBO<T>> children;

    public void fromModel(OptionModel model, Locale locale, Function<String, T> function) {
        String boName = Objects.equals(locale, Locale.US) ? model.getEnName() : model.getZhName();
        this.setName(boName);
        this.setCode(function.apply(model.getCode()));
    }
}
