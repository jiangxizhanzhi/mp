package cn.cyberpeace.mp.agent.snmp.model;

import lombok.Data;

import java.util.List;

@Data
public class Command {
    private String command;
    private List<Param> params;
}
