package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Switches {

    private String name;

    private String management_ip;

    private String status;

    private Integer interfaceNums;

    private List<String> interfaceUpDetails;

    private Integer interfaceUpNums;

    private Integer vlanNums;

    private List<InterfaceDetail> interfaceDetails;
}
