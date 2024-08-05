package cn.cyberpeace.mp.agent.snmp.service;

import lombok.Data;

import java.util.List;

@Data
public class CommandConfig {
    private List<Command> commands;
}
