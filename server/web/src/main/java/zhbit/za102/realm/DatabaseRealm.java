package zhbit.za102.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import zhbit.za102.bean.User;
import zhbit.za102.service.PermissionService;
import zhbit.za102.service.RoleService;
import zhbit.za102.service.UserService;

import java.util.Set;

/**
 * 2个方法分别是角色验证、权限验证
 */

public class DatabaseRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName = (String) principalCollection.getPrimaryPrincipal();
        Set<String> permissions = permissionService.listPermissions(userName);
        Set<String> roles = roleService.listRoleNames(userName);

        // 授权对象
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();
        s.setStringPermissions(permissions);
        s.setRoles(roles);
        return s;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 获取账号密码
        UsernamePasswordToken t = (UsernamePasswordToken) token;
        String userName = token.getPrincipal().toString();
        String password = new String(t.getPassword());
        // 获取数据库中的密码
        User user = userService.getByName(userName);
        String passwordInDB = user.getPassword();
        String salt = user.getSalt();
        System.out.println(passwordInDB);
        //进行salt的2次加密
        String passwordEncoded = new SimpleHash("md5",password,salt,2).toString();
        System.out.println(passwordEncoded);
        System.out.println(user);
        //用户是否存在、验证密码
        if(null==user || !passwordEncoded.equals(passwordInDB))
            throw new AuthenticationException();
       //进行自动校验
        SimpleAuthenticationInfo a = new SimpleAuthenticationInfo(userName, passwordInDB, ByteSource.Util.bytes(salt),
                getName());
        return a;
    }

}