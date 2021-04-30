package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCondition implements BaseCondition {

    private String[] groupFields;

    @Override
    public boolean isEmpty() {
        return groupFields == null || groupFields.length == 0;
    }
}
