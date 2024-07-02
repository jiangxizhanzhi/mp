package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GroupCondition
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCondition
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCondition implements BaseCondition {

    private String[] groupFields;

    /**
     * isEmpty
     *
     * @return boolean
     */
    @Override
    public boolean isEmpty() {
        return groupFields == null || groupFields.length == 0;
    }
}
