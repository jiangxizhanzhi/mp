package cn.cyberpeace.mp.base.service.param;

/**
 * 分页兼容查询参数
 */
public interface CompatiblePageQueryParams extends PageQueryParams, CompatibleQueryParams {

    /**
     * ensureCompatible
     *
     * @param queryParams
     */
    static void ensureCompatible(CompatiblePageQueryParams queryParams) {
        Integer perPage = queryParams.getPerPage();
        if (perPage != null) {
            queryParams.setPageSize(perPage);
        }
    }

    /**
     * getPerPage
     *
     * @return
     */
    Integer getPerPage();

    /**
     * setPerPage
     *
     * @param perPage
     */
    void setPerPage(Integer perPage);

    /**
     * ensure
     */
    @Override
    default void ensure() {
        ensureCompatible(this);
    }

}
