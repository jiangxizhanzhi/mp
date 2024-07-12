 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClientAuthenticationToken.java.
 // File size 1916.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.client;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * @ClassName ClientAuthenticationToken
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see AbstractAuthenticationToken
 */
public class ClientAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    private Object credentials;

    /**
     * ClientAuthenticationToken
     *
     * @param clientId
     * @param credentials
     */
    public ClientAuthenticationToken(String clientId, Object credentials) {
        super(null);
        this.principal = clientId;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public ClientAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }


    /**
     * getCredentials
     *
     * @return
     */
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    /**
     * getPrincipal
     *
     * @return
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
