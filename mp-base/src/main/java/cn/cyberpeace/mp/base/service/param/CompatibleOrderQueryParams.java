package cn.cyberpeace.mp.base.service.param;

/**
 * 排序查询参数
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
