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

    public PaginationCondition loadFromPageParams(int page, int pageSize) {
        if (pageSize > 0) {
            limit = pageSize;
        } else {
            limit = null;
            offset = null;
            return this;
        }

        if (page > 0) {
            offset = (page - 1) * limit;
        } else {
            offset = null;
        }

        return this;
    }

    @Override
    public boolean isEmpty() {
        return limit == null || limit <= 0;
    }
}
