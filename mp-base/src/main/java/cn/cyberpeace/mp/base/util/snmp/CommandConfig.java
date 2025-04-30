package cn.cyberpeace.mp.base.util.snmp;

import lombok.Data;

import java.util.List;

@Data
public class CommandConfig {
    private List<Command> commands;
}