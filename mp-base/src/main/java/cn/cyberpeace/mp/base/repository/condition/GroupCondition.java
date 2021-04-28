package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCondition extends BaseCondition {

    private String[] groupFields;

    @Override
    public Boolean isEmpty() {
        return groupFields == null || groupFields.length == 0;
    }
}
