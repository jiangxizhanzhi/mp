package cn.cyberpeace.mp.devopscluster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDeleteDTO extends BaseDTO {

    
    private List<String> ids;
}
