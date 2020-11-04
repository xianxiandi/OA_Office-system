package com.web.oa.controller;

import com.web.oa.pojo.*;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.SysService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SysService sysService;

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){
        String exceptionName = (String) request.getAttribute("shiroLoginFailure");
        if (exceptionName!=null){
            if (UnknownAccountException.class.getName().equals(exceptionName)){
                model.addAttribute("errorMsg","用户账号不存在!");
            }else if (IncorrectCredentialsException.class.getName().equals(exceptionName)){
                model.addAttribute("errorMsg","密码不正确!");
            }else if ("randomcodeError".equals(exceptionName)){
                model.addAttribute("errorMsg","验证码不正确!");
            }else {
                model.addAttribute("errorMsg","未知错误!");
            }
        }
        return "login";
    }
    @RequestMapping("/findUserList")
    public ModelAndView findUserList(){
        ModelAndView mv=new ModelAndView();
       List<SysRole> allRoles= sysService.findAllRoles();
       List<EmployeeCustom> list=employeeService.findUserAndRoleList();
       mv.addObject("userList",list);
       mv.addObject("allRoles",allRoles);
       mv.setViewName("userlist");
        return mv;
    }
    @RequestMapping("/assignRole")
    @ResponseBody
    public Map<String,String> assignRole(String roleId,String userId){
        Map<String,String> map=new HashMap<String, String>();
        try{
            employeeService.updateEmployeeRole(roleId,userId);
            map.put("msg","权限分配成功!");
        }catch (Exception e){
            e.printStackTrace();
            map.put("msg","权限分配失败!");
        }
        return map;
    }

    @RequestMapping("/viewPermissionByUser")
    @ResponseBody
    public SysRole viewPermissionByUser(String userName){
        SysRole sysRole=sysService.findRolesAndPermissionByUserId(userName);
        return sysRole;
    }

    @RequestMapping("/findNextManager")
    @ResponseBody
    public List<Employee> findNextManager(int level){
        level++;
        List<Employee> list=employeeService.findEmployeeByLevel(level);
        return list;
    }
    @RequestMapping("/saveUser")
    public String saveUser(Employee employee, ModelMap model){
        int i = employeeService.saveUser(employee);
        return "redirect:/findUserList";
    }
    @RequestMapping("/toAddRole")
    public ModelAndView toAddRole(){
        ModelAndView mv=new ModelAndView();
        List<MenuTree> allPremissions=sysService.loadMenuTree();
        List<SysPermission> menus=sysService.findAllMenus();
        List<SysRole> permissionList=sysService.findRolesAndPermissions();

        mv.addObject("allPermissions",allPremissions);
        mv.addObject("menuTypes",menus);
        mv.addObject("roleAndPermissionsList",permissionList);
        mv.setViewName("rolelist");
        return mv;

    }

    @RequestMapping("/saveRoleAndPermissions")
    public String saveRoleAndPermissions(SysRole role,int[] permissionIds){
        //设置role主键，使用uuid
        String uuid = UUID.randomUUID().toString();
        role.setId(uuid);
        role.setAvailable("1");
        sysService.addRoleAndPermissions(role,permissionIds);

        return "redirect:/toAddRole";
    }
    @RequestMapping("/saveSubmitPermission")
    public String saveSubmitPermission(SysPermission permission){
        if (permission.getAvailable()==null){
            permission.setAvailable("0");
        }
        sysService.addSysPermisson(permission);
        return "redirect:/toAddRole";
    }

    @RequestMapping("/findRoles")
    public ModelAndView findRoles(){
        ActiveUser activeUser= (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<SysRole> roles=sysService.findAllRoles();
        List<MenuTree> allMenuAndPermissions=sysService.getAllMenuAndPermision();
        ModelAndView mv=new ModelAndView();
        mv.addObject("allRoles",roles);
        mv.addObject("activeUser",activeUser);
        mv.addObject("allMenuAndPermissions",allMenuAndPermissions);
        mv.setViewName("permissionlist");
        return mv;
    }
    @RequestMapping("/loadMyPermissions")
    @ResponseBody
    public List<SysPermission> loadMyPermissions(String roleId){
        List<SysPermission> list = sysService.findPermissionsByRoleId(roleId);
        return list;
    }
    @RequestMapping("/updateRoleAndPermission")
    public String updateRoleAndPermission(String roleId,int[] permissionIds){
        sysService.updateRoleAndPermissions(roleId, permissionIds);
        return "redirect:/findRoles";
    }
    @RequestMapping("/delete.action")
    public String deleteRole(String roleId){
        sysService.deleteRole(roleId);
        return "redirect:/findRoles";
    }

    @RequestMapping("/delUser")
    public String delUser(String userId){
        employeeService.deleteUser(userId);
        return "redirect:/findUserList";
    }




}
