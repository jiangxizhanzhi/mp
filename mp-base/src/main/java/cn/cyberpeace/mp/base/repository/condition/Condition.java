package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition implements BaseCondition {

    private Boolean distinct = false;

    private CustomCondition custom;

    private AndCondition andi;

    private GroupCondition group;

    private OrderCondition order;

    private PaginationCondition pagination;

    public boolean isWhereFull() {
        return custom != null && !custom.isEmpty() && andi != null && andi.isEmpty();
    }

    public boolean isWhereEmpty() {
        if (custom != null && !custom.isEmpty()) {
            return false;
        }

        return andi == null || andi.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        if (!isWhereEmpty()) {
            return false;
        }
        if (group != null && !group.isEmpty()) {
            return false;
        }
        if (order != null && !order.isEmpty()) {
            return false;
        }
        return pagination == null || pagination.isEmpty();
    }
}
