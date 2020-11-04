package com.web.oa.utils;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.service.EmployeeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class ManagerTaskHandler implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //spring容器
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        ActiveUser activeUser= (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        EmployeeService employeeService = (EmployeeService) context.getBean("employeeService");
       Employee manager= employeeService.findEmployeeManager(activeUser.getManagerId());
       delegateTask.setAssignee(manager.getName());

    }
}
