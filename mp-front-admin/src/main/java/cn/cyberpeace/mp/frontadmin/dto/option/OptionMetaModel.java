package cn.cyberpeace.mp.frontadmin.dto.option;

import lombok.Data;


@Data
public class OptionMetaModel {

    public static Integer OPTION_VISIBLE_YES = 1;

    public static Integer OPTION_VISIBLE_NO = 0;

    private String activKeys;

    private Integer visible;

}
