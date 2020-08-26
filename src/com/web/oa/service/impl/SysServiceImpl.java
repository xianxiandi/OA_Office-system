package com.web.oa.service.impl;

import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.pojo.MenuTree;
import com.web.oa.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysServiceImpl implements SysService {
    @Autowired
    private SysPermissionMapperCustom sysPermissionMapperCustom;


    @Override
    public List<MenuTree> loadMenuTree() {

        return sysPermissionMapperCustom.getMenuTree();
    }
}
