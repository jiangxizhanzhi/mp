package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName CompatibleOrderQueryParams
 * @Description 排序查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see OrderQueryParams
 * @see CompatibleQueryParams
 */
public interface CompatibleOrderQueryParams extends OrderQueryParams, CompatibleQueryParams {

    /**
     * ensureCompatible
     *
     * @param queryParams
     */
    static void ensureCompatible(CompatibleOrderQueryParams queryParams) {
        String[] sort = queryParams.getSort();
        if (sort != null && sort.length > 0) {
            queryParams.setOrders(sort);
        }
    }

    /**
     * getSort
     *
     * @return
     */
    String[] getSort();

    /**
     * setSort
     *
     * @param sort
     */
    void setSort(String[] sort);

    /**
     * ensure
     */
    default void ensure() {
        ensureCompatible(this);
    }
}
