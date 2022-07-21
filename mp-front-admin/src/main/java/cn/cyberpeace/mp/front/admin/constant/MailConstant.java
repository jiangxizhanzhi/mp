package cn.cyberpeace.mp.front.admin.constant;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/19 09:55
 */
public class MailConstant {

    public static final String CONTENT_HEADER_TEMPLATE =
            "<html>\n" +
            " <head>\n" +
            "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
            "</head>\n" +
            "<body style=\"margin: 0; padding: 50;\">\n" +
            " <table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"1000\">\n" +
            "    <tr>\n" +
            "        <th>场景名称</th>\n" +
            "        <th>场景ID</th>\n" +
            "        <th>高危攻击次数</th>\n" +
            "        <th>开始时间</th>\n" +
            "        <th>结束时间</th>\n" +
            "    </tr>\n";



    public static final String CONTENT_TAIL_TEMPLATE =
            " </table>\n" +
            "</body>\n" +
            "</html>";


    public static final String COLUMN_TEMPLATE =
            "    <tr>\n" +
            "        <td align=\"center\">${name}</td>\n" +
            "        <td align=\"center\">${id}</td>\n" +
            "        <td align=\"center\">${highRiskCount}</td>\n" +
            "        <td align=\"center\">${startTime}</td>\n" +
            "        <td align=\"center\">${endTime}</td>\n" +
            "    </tr>\n";
}
