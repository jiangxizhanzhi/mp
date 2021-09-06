package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flavor {

    private String id;

    private String uuid;

    private String name;

    private String description;

    private Integer cpu;

    private Integer ram;

    private Integer disk;

    private String createdAt;

    private String updatedAt;

    private Object user;

    private Object project;
}
