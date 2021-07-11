package cn.cyberpeace.mp.situationaware.bo.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackSuricata{

    
    private List<String> category;

    
    private List<String> categoryZh;

    
    private List<Integer> severity;

}
