package cn.cyberpeace.mp.auth.dto.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/16 20:13
 */
@Data
public class TargetActivDto extends TargetModel {

    // 表示这个菜单不受系统激活项的限制，如帮助中心等，无论系统激活项如何选择，都应该显示
    public static final String NO_AUTH = "no_auth";

    private List<String> active;

    private List<String> edition;

}
