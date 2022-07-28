package cn.cyberpeace.mp.common.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.common.bo.DropdownBO;
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
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_common_dropdown")
public class DropdownModel extends BaseModel {
    private String code;
    private String zhName;
    private String enName;
    private Integer sort;
    private Integer type;

    public DropdownBO toBO(Locale locale) {
        String name = Objects.equals(locale, Locale.US) ? this.getEnName() : this.getZhName();
        String code = this.getCode();
        return new DropdownBO(code, name);
    }

}

