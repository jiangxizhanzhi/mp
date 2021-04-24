package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCondition extends BaseCondition {

    private String[] orders;

    @Override
    public Boolean isEmpty() {
        return orders == null || orders.length == 0;
    }
}
