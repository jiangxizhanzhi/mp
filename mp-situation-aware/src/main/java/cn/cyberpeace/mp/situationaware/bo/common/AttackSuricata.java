package cn.cyberpeace.mp.situationaware.bo.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttackSuricata{

    /**
     * 分类
     */
    private List<String> category;

    /**
     * 分类名称
     */
    private List<String> categoryZh;

    /**
     * 严重性
     */
    private List<Integer> severity;

}
