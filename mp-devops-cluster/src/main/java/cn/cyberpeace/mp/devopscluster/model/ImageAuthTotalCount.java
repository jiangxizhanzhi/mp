package cn.cyberpeace.mp.devopscluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageAuthTotalCount {

    private String target;

    private Integer total;
}
