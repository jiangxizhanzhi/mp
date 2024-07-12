 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PaginationCondition.java.
 // File size 631.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.repository.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PaginationCondition
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCondition
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationCondition implements BaseCondition {

    private Integer limit;

    private Integer offset;

    /**
     * isEmpty
     *
     * @return boolean
     */
    @Override
    public boolean isEmpty() {
        return limit == null || limit <= 0;
    }
}
