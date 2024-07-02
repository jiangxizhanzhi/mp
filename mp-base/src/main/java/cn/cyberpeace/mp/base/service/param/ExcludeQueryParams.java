package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName ExcludeQueryParams
 * @Description id排除列表查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
