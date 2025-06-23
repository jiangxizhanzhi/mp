 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Condition.java.
 // File size 1505.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Condition
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCondition
 */
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

    /**
     * isWhereFull
     *
     * @return boolean
     */
    public boolean isWhereFull() {
        return custom != null && !custom.isEmpty() && andi != null && andi.isEmpty();
    }

    /**
     * isWhereEmpty
     *
     * @return boolean
     */
    public boolean isWhereEmpty() {
        if (custom != null && !custom.isEmpty()) {
            return false;
        }

        return andi == null || andi.isEmpty();
    }

    /**
     * isEmpty
     *
     * @return boolean
     */
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
