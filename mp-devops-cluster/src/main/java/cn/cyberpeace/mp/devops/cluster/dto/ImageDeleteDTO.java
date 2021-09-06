package cn.cyberpeace.mp.devops.cluster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDeleteDTO extends BaseDTO {

    /**
     * id集合
     */
    private List<String> ids;
}
