package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlavorConvertModel {

    private String id;

    private String uuid;

    private String name;

    private String description;

    private Integer cpu;

    private Integer ram;

    private Integer disk;
}
