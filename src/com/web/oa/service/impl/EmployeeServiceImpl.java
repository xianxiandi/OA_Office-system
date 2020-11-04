package com.web.oa.service.impl;

import com.web.oa.mapper.EmployeeMapper;
import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.mapper.SysRoleMapper;
import com.web.oa.mapper.SysUserRoleMapper;
import com.web.oa.pojo.*;
import com.web.oa.service.EmployeeService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysPermissionMapperCustom sysPermissionMapperCustom;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;


    @Override
    public Employee findEmployeeByName(String name) {
        EmployeeExample employeeExample=new EmployeeExample();
        EmployeeExample.Criteria criteria = employeeExample.createCriteria();
        criteria.andNameEqualTo(name);
        List<Employee> employees =employeeMapper.selectByExample(employeeExample);
        if (employees!=null&&employees.size()>0){
            return employees.get(0);
        }
        return null;
    }

    @Override
    public Employee findEmployeeManager(Long managerId) {
        Employee employee = employeeMapper.selectByPrimaryKey(managerId);
        return employee;
    }

    @Override
    public List<EmployeeCustom> findUserAndRoleList() {
        return sysPermissionMapperCustom.findUserAndRoleList();
    }

    @Override
    public void updateEmployeeRole(String roleId, String userId) {
        SysUserRoleExample example = new SysUserRoleExample();
        SysUserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andSysUserIdEqualTo(userId);
        SysUserRole userRole = sysUserRoleMapper.selectByExample(example).get(0);
        if(userRole!=null&&!userRole.equals("")){
            userRole.setSysRoleId(roleId);
            sysUserRoleMapper.updateByPrimaryKey(userRole);
        }

    }

    @Override
    public List<Employee> findEmployeeByLevel(int level) {
        EmployeeExample example=new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        criteria.andRoleEqualTo(level);
        List<Employee> list = employeeMapper.selectByExample(example);
        return list;
    }

    @Override
    public int saveUser(Employee employee) {
        SysUserRole ur=new SysUserRole();
        String salt="eteokues";
        Md5Hash hash = new Md5Hash(employee.getPassword(), salt, 2);
        employee.setPassword(hash.toString());
        employee.setSalt(salt);
       int i= employeeMapper.insert(employee);

        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample(null);
        int count=0;
        for (SysUserRole sysUserRole : sysUserRoles) {
            count++;
        }
        ur.setId(String.valueOf(count+1));
       ur.setSysUserId(employee.getName());
       ur.setSysRoleId(String.valueOf(employee.getRole()));
        int insert = sysUserRoleMapper.insert(ur);
        return i;
    }

    @Override
    public void deleteUser(String userId) {
        Employee employee = employeeMapper.selectByPrimaryKey(Long.valueOf(userId));
        SysUserRoleExample userRoleExample = new SysUserRoleExample();
        SysUserRoleExample.Criteria criteria1 = userRoleExample.createCriteria();
        criteria1.andSysUserIdEqualTo(userId);
        employeeMapper.deleteByPrimaryKey(Long.valueOf(userId));
        sysUserRoleMapper.deleteByExample(userRoleExample);
    }

}
