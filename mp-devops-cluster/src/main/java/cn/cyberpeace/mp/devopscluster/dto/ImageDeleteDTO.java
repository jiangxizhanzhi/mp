package cn.cyberpeace.mp.devopscluster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ImageDeleteDTO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseDTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDeleteDTO extends BaseDTO {

    /**
     * id集合
     */
    private List<String> ids;
}
