package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.context.http.HttpContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Description
 * @Author maojun
 * @Date 2021/8/31
 */
public class SysContextUtilsTest {

    @Test
    public void httpContextTest() {
        String requestId = RandomStringUtils.random(10, true, true);
        SysContextHolder.set(new HttpContext(requestId));
        Assert.assertNotNull(SysContextUtils.getCallChainId());
        Assert.assertEquals(requestId, SysContextUtils.getCallChainId());
        SysContextHolder.clear();

        SysContextHolder.set(null);
        Assert.assertNotNull(SysContextUtils.getCallChainId());
        SysContextHolder.clear();
    }
}
