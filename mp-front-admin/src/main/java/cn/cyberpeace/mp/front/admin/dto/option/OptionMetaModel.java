package cn.cyberpeace.mp.front.admin.dto.option;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/15 14:39
 */
@Data
public class OptionMetaModel {

    public static Integer OPTION_VISIBLE_YES = 1;

    public static Integer OPTION_VISIBLE_NO = 0;

    private String activKeys;

    private Integer visible;

}
