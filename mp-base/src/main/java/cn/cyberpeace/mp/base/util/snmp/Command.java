package cn.cyberpeace.mp.base.util.snmp;

import lombok.Data;

import java.util.List;

@Data
public class Command {
    private String command;
    private List<Param> params;
}
