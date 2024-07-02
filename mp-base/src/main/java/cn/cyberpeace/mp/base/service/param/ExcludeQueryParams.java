package cn.cyberpeace.mp.base.service.param;

/**
 * id排除列表查询参数
 */
public interface ExcludeQueryParams {

    /**
     * getExclude
     *
     * @return
     */
    String[] getExclude();

    /**
     * setExclude
     *
     * @param exclude
     */
    void setExclude(String[] exclude);

}
