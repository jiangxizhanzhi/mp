package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition extends BaseCondition {

    public CustomCondition custom;

    public AndCondition andi;

    public SearchCondition search;

    public GroupCondition group;

    public OrderCondition order;

    public PaginationCondition pagination;

    public Boolean isWhereFull() {
        return custom != null && !custom.isEmpty() && andi != null && andi.isEmpty() &&
                search != null && !search.isEmpty();
    }

    public Boolean isWhereEmpty() {
        if (custom != null && !custom.isEmpty()) {
            return false;
        }

        if (andi != null && !andi.isEmpty()) {
            return false;
        }

        if (search != null && !search.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean isEmpty() {
        if (!isWhereEmpty()) {
            return false;
        }
        if (group != null && !group.isEmpty()) {
            return false;
        }
        if (order != null && !order.isEmpty()) {
            return false;
        }
        if (pagination != null && !pagination.isEmpty()) {
            return false;
        }

        return true;
    }
}
