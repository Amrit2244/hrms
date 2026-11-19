package com.company.hrms.bootstrap;
import com.company.hrms.organization.Organization;
import com.company.hrms.user.*;
public final class UserAccountFactory {
    private UserAccountFactory() {}
    public static UserAccount create(Organization org,String username,String email,String hash,Role role) {
        return new UserAccount(org, username, email, hash, role);
    }
}
