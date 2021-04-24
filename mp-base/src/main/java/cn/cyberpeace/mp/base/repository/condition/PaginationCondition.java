package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationCondition extends BaseCondition {

    private Integer limit;

    private Integer offset;

    public PaginationCondition loadFromPageParams(Integer page, Integer pageSize) {
        if (pageSize != null && pageSize > 0) {
            limit = pageSize;
        } else {
            limit = null;
            offset = null;
            return this;
        }

        if (page != null && page > 0) {
            offset = (page - 1) * limit;
        } else {
            offset = null;
        }

        return this;
    }

    @Override
    public Boolean isEmpty() {
        return limit == null || limit <= 0;
    }
}
