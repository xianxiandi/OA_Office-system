package com.web.oa.service;

import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

import java.util.List;

public interface SysService {
    public List<MenuTree> loadMenuTree();

    List<SysPermission> findPermissionListByUserId(String userid) throws Exception;

    List<SysRole> findAllRoles();

    SysRole findRolesAndPermissionByUserId(String userName);

    List<MenuTree> getAllMenuAndPermision();

    List<SysPermission> findPermissionsByRoleId(String roleId);

    void updateRoleAndPermissions(String roleId, int[] permissionIds);

    List<SysPermission> findAllMenus();

    List<SysRole> findRolesAndPermissions();

    void addRoleAndPermissions(SysRole role, int[] permissionIds);

    void addSysPermisson(SysPermission permission);

    void deleteRole(String roleId);

}
