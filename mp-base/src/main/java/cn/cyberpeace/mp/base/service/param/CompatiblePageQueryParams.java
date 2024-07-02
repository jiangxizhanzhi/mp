package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName CompatiblePageQueryParams
 * @Description 分页兼容查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PageQueryParams
 * @see CompatibleQueryParams
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
