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

    private List<String> active;

    private List<String> edition;

}
