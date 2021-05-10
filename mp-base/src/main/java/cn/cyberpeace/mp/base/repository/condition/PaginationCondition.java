package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationCondition implements BaseCondition {

    private Integer limit;

    private Integer offset;

    @Override
    public boolean isEmpty() {
        return limit == null || limit <= 0;
    }
}
