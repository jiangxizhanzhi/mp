package cn.cyberpeace.mp.auth.extensions.security.service;

import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    public AuthUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username:{}", username);
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        UserModel user;
        try {
            user = userService.getByUserName(username);
        } catch (Exception e) {
            log.error("Get user by username[{}] error: {}", username, e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
        if (user == null) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        Set<GrantedAuthority> authoritySet = loadAuthorities(user.getId());

        return new AuthUserDetails(user, authoritySet);
    }


    public UserDetails loadUserByMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        UserModel user;
        try {
            user = userService.getByMobile(mobile);
        } catch (Exception e) {
            log.error("Get user by mobile, error: {}", e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
        if (user == null) {
            throw new UsernameNotFoundException(StringUtils.EMPTY);
        }

        Set<GrantedAuthority> authoritySet = loadAuthorities(user.getId());

        return new AuthUserDetails(user, authoritySet);
    }


    /**
     * @param userId
     * @param targetType   @see cn.cyberpeace.mp.auth.model.TargetModel
     * @return
     */
    public Set<GrantedAuthority> loadAuthorities(String userId) {
        List<RoleModel> roles = userRoleService.listByUserId(userId);
        Set<GrantedAuthority> authoritySet = new HashSet<>();
        roles.forEach(r -> {
            authoritySet.add(new SimpleGrantedAuthority("ROLE_" + r.getIdentifier()));
        });


        // 加载权限
        List<TargetModel> targets = userPermissionService.listByUserId(userId);
        targets.forEach(t -> {
            authoritySet.add(new SimpleGrantedAuthority(t.getIdentifier()));
        });
        return authoritySet;
    }


}
