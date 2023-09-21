package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.sceneplugincloud.bo.base.Leak;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @ClassName ElementLeaksDto
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/21 19:02
 **/
@Data
@Accessors(chain = true)
public class ElementLeaksDto {

    private String id;

    private String systemInfo;

    private List<Leak> leaks;
}
