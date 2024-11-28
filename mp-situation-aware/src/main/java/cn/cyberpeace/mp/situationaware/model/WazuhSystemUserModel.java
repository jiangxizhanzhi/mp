package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhSystemUserModel {

    private String agentIp;

    private String agentName;

    @JSONField(name = "comment")
    private String comment;

    @JSONField(name = "shell")
    private String shell;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "group_name")
    private String groupName;

    @JSONField(name = "group_id")
    private String groupId;

    @JSONField(name = "id")
    private String id;

    @JSONField(name = "dir")
    private String dir;

    private String description;

    private LocalDateTime time;

    @Getter
    @Setter
    public static class Output {
        private String agentIp;
        private String time;
        private String action;
        private String name;
        private String groupName;
    }

    public Output toOutput() {
        Output output = new Output();
        output.setAgentIp(this.agentIp);
        output.setAction(this.shell);
        output.setName(this.name);
        output.setGroupName(this.groupName);

        var a = TimeUtils.localDateTimeToUtcString(this.getTime());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(a, inputFormatter);
        LocalDateTime localDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String aaa = localDateTime.format(outputFormatter);
        output.setTime(aaa);

        return output;
    }
}
