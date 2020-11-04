package com.web.oa.service;

import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeCustom;

import java.util.List;

public interface EmployeeService {
    Employee findEmployeeByName(String name);

    Employee findEmployeeManager(Long managerId);

    List<EmployeeCustom> findUserAndRoleList();

    void updateEmployeeRole(String roleId, String userId);

    List<Employee> findEmployeeByLevel(int level);

    int saveUser(Employee employee);

    void deleteUser(String userId);
}
