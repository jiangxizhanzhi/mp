 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MobilePasswordAuthenticationToken.java.
 // File size 1604.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.mobile;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

 /**
  * 移动密码认证令牌
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public class MobilePasswordAuthenticationToken  extends AbstractAuthenticationToken {

     /**
      * 串口版本uid
      */
     private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

     /**
      * 主要
      */
     private final Object principal;

     /**
      * 凭证
      */
     private Object credentials;

     /**
      * 移动密码认证令牌
      *
      * @param mobile      移动
      * @param credentials 凭证
      */
     public MobilePasswordAuthenticationToken(String mobile, Object credentials) {
        super(null);
        this.principal = mobile;
        this.credentials = credentials;
        setAuthenticated(false);
    }

     /**
      * Creates a token with the supplied array of authorities.
      *
      * @param principal   主要
      * @param credentials 凭证
      * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
      */
     public MobilePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

     /**
      * 获得证书
      *
      * @return {@link Object }
      */
     @Override
    public Object getCredentials() {
        return this.credentials;
    }

     /**
      * 得到主
      *
      * @return {@link Object }
      */
     @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
