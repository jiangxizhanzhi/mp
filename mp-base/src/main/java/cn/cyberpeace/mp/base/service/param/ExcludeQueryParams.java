package cn.cyberpeace.mp.base.service.param;

/**
 * id排除列表查询参数
 */
public interface ExcludeQueryParams {

    String[] getExclude();

    void setExclude(String[] exclude);

}
