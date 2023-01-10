package cn.cyberpeace.mp.collection.bo;

import lombok.Data;

@Data
public class AttackTypeBO {
    private String enName;

    private String zhName;

    private long count;

    private long flowSize ;

    private double percent;
}
