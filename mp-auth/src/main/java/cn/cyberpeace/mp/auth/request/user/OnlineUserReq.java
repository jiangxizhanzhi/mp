package cn.cyberpeace.mp.auth.request.user;

import lombok.Data;

import java.util.List;

@Data
public class OnlineUserReq {
    private List<String> ids;
}
