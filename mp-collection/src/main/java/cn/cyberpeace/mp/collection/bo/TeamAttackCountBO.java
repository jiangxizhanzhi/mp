package cn.cyberpeace.mp.collection.bo;

import java.util.List;

import lombok.Data;

/**
 * @Description: 
 * @author zhangjiaxin
 * @date 2022年10月25日下午7:12:36 
 */

@Data
public class TeamAttackCountBO {

	private String ip;
	
    private long count;

    private List<CountBO> attackTargetIps;

    private List<CountBO> attackTypes;
    
}
