package com.style.admin.modules.security;

import com.style.utils.collect.ListUtils;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.crypto.CryptoException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import java.util.ArrayList;
import java.util.Collection;

public class WebSecurityManager extends DefaultWebSecurityManager {

    public WebSecurityManager() {
        super();
    }

    public WebSecurityManager(Realm singleRealm) {
        super(singleRealm);
    }

    public WebSecurityManager(Collection<Realm> realms) {
        super(realms);
    }

    /**
     * 设置多域认证，授权
     */
    @Override
    public void setRealms(Collection<Realm> realms) {
        super.setRealms(realms);
        // 多域环境下，防止重复授权。
        if (this.getAuthorizer() instanceof ModularRealmAuthorizer) {
            ModularRealmAuthorizer authorizer = (ModularRealmAuthorizer) this.getAuthorizer();
            ArrayList<Realm> authorizerReams = ListUtils.newArrayList();
            authorizerReams.add(realms.iterator().next());
            authorizer.setRealms(authorizerReams);
        }
    }

    @Override
    protected PrincipalCollection getRememberedIdentity(SubjectContext subjectContext) {
        try {
            return super.getRememberedIdentity(subjectContext);
        } catch (CryptoException e) {
            return null;
        }
    }
}