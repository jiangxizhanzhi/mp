package cn.cyberpeace.mp.base.service.param;

/**
 * 分页查询参数
 */
public interface PageQueryParams {

    /**
     * getPage
     *
     * @return
     */
    Integer getPage();

    /**
     * getPageSize
     *
     * @return
     */
    Integer getPageSize();

    /**
     * setPage
     *
     * @param page
     */
    void setPage(Integer page);

    /**
     * setPageSize
     *
     * @param pageSize
     */
    void setPageSize(Integer pageSize);

}
