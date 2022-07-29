package cn.cyberpeace.mp.common.bo;

import cn.cyberpeace.mp.common.model.OptionModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Objects;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionBO {
    private String code;
    private String name;

    public void fromModel(OptionModel model, Locale locale) {
        String name = Objects.equals(locale, Locale.US) ? model.getEnName() : model.getZhName();
        this.setName(name);
        this.setCode(model.getCode());
    }
}
