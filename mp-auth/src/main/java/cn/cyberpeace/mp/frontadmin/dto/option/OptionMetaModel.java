package cn.cyberpeace.mp.frontadmin.dto.option;

import lombok.Data;

/**
 * @ClassName OptionMetaModel
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/8/15 14:39
 * @Version 1.0.0
 */
@Data
public class OptionMetaModel {

    public static Integer OPTION_VISIBLE_YES = 1;

    public static Integer OPTION_VISIBLE_NO = 0;

    private String activKeys;

    private Integer visible;

}
