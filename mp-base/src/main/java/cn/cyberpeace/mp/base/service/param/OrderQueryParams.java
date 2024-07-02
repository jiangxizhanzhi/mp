package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName OrderQueryParams
 * @Description 排序查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface OrderQueryParams {

    /**
     * getOrders
     *
     * @return
     */
    String[] getOrders();

    /**
     * setOrders
     *
     * @param fields
     */
    void setOrders(String[] fields);

}
