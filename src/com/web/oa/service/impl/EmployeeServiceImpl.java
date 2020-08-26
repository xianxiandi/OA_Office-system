package com.web.oa.service.impl;

import com.web.oa.mapper.EmployeeMapper;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeExample;
import com.web.oa.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;
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
}
