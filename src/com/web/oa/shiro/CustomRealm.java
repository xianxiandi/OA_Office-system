package com.web.oa.shiro;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.SysService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SysService sysService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username= (String) token.getPrincipal();

        Employee user = null;
        try {
            user = employeeService.findEmployeeByName(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user==null){
            return null;
        }
        List<MenuTree> menuTrees = sysService.loadMenuTree();
        ActiveUser activeUser=new ActiveUser();
        activeUser.setId(user.getId());
        activeUser.setUserid(user.getName());
        activeUser.setUsercode(user.getName());
        activeUser.setUsername(user.getName());
        activeUser.setManagerId(user.getManagerId());
        activeUser.setMenuTree(menuTrees);
        String password_db=user.getPassword();
        String salt=user.getSalt();
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(activeUser, password_db, ByteSource.Util.bytes(salt), "CustomRealm");
        return simpleAuthenticationInfo;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        ActiveUser activeUser= (ActiveUser) principalCollection.getPrimaryPrincipal();
        //查询数据库认证用户拥有的角色和权限
        List<SysPermission> permissions=null;
        try {
            permissions=sysService.findPermissionListByUserId(activeUser.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> permissionList=new ArrayList<>();
        for (SysPermission permission : permissions) {
            permissionList.add(permission.getPercode());
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addStringPermissions(permissionList);
        return simpleAuthorizationInfo;
    }


}
