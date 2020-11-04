package com.web.oa.service.impl;

import com.web.oa.mapper.*;
import com.web.oa.pojo.*;
import com.web.oa.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SysServiceImpl implements SysService {
    @Autowired
    private SysPermissionMapperCustom sysPermissionMapperCustom;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private EmployeeMapper employeeMapper;


    @Override
    public List<MenuTree> loadMenuTree() {

        return sysPermissionMapperCustom.getMenuTree();
    }

    @Override
    public List<SysPermission> findPermissionListByUserId(String userid) throws Exception {


        return sysPermissionMapperCustom.findPermissionListByUserId(userid);
    }

    @Override
    public List<SysRole> findAllRoles() {

        return sysRoleMapper.selectByExample(null);
    }

    @Override
    public SysRole findRolesAndPermissionByUserId(String userName) {

        return sysPermissionMapperCustom.findRoleAndPermissionListByUserId(userName);
    }

    @Override
    public List<MenuTree> getAllMenuAndPermision() {

        return sysPermissionMapperCustom.getAllMenuAndPermision();
    }

    @Override
    public List<SysPermission> findPermissionsByRoleId(String roleId) {

        return sysPermissionMapperCustom.findPermissionsByRoleId(roleId);
    }

    @Override
    public void updateRoleAndPermissions(String roleId, int[] permissionIds) {
        //先删除角色权限关系表中角色的权限关系
        SysRolePermissionExample example = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);
        sysRolePermissionMapper.deleteByExample(example);
        //重新创建角色权限关系
        for (Integer pid : permissionIds) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            String udd= UUID.randomUUID().toString();
            sysRolePermission.setId(udd);
            sysRolePermission.setSysRoleId(roleId);
            sysRolePermission.setSysPermissionId(pid.toString());
            sysRolePermissionMapper.insert(sysRolePermission);
        }
    }

    @Override
    public List<SysPermission> findAllMenus() {
        SysPermissionExample example = new SysPermissionExample();
        SysPermissionExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo("menu");

        return sysPermissionMapper.selectByExample(example);
    }

    @Override
    public List<SysRole> findRolesAndPermissions() {

        return sysPermissionMapperCustom.findRoleAndPermissionList();
    }

    @Override
    public void addRoleAndPermissions(SysRole role, int[] permissionIds) {
        //添加角色
         sysRoleMapper.insert(role);
        //添加角色和权限关系表
        for (int i = 0; i < permissionIds.length; i++) {
            SysRolePermission rolePermission = new SysRolePermission();
            //16进制随机码
            String uuid = UUID.randomUUID().toString();
            rolePermission.setId(uuid);
            rolePermission.setSysRoleId(role.getId());
            rolePermission.setSysPermissionId(permissionIds[i]+"");
            sysRolePermissionMapper.insert(rolePermission);
        }

    }

    @Override
    public void addSysPermisson(SysPermission permission) {
        sysPermissionMapper.insert(permission);
    }

    @Override
        public void deleteRole(String roleId) {

            SysRolePermissionExample example = new SysRolePermissionExample();
            SysRolePermissionExample.Criteria criteria = example.createCriteria();
            criteria.andSysRoleIdEqualTo(roleId);
            SysUserRoleExample userRoleExample = new SysUserRoleExample();
            SysUserRoleExample.Criteria criteria1 = userRoleExample.createCriteria();
            criteria1.andSysRoleIdEqualTo(roleId);

             sysRoleMapper.deleteByPrimaryKey(roleId);
            if (example!=null&&!example.equals("")){
                sysRolePermissionMapper.deleteByExample(example);
            }
            if (userRoleExample!=null&&!userRoleExample.equals("")){
                List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample(userRoleExample);

                if (sysUserRoles.size()>0) {
                    sysUserRoles.get(0).setSysRoleId("1");
                    sysUserRoleMapper.updateByPrimaryKey(sysUserRoles.get(0));
                }
            }
    }

}
