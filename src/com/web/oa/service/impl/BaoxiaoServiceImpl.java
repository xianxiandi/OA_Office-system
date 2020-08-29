package com.web.oa.service.impl;

import com.web.oa.mapper.BaoxiaoBillMapper;
import com.web.oa.pojo.BaoxiaoBill;
import com.web.oa.pojo.BaoxiaoBillExample;
import com.web.oa.service.BaoxiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaoxiaoServiceImpl implements BaoxiaoService {
    @Autowired
    private BaoxiaoBillMapper baoxiaoBillMapper;
    @Override
    public void saveBaoxiao(BaoxiaoBill baoxiaoBill) {
        Long id=baoxiaoBill.getId();
        if (id==null){
            baoxiaoBillMapper.insert(baoxiaoBill);
        }else {
            baoxiaoBillMapper.updateByPrimaryKey(baoxiaoBill);
        }
    }

    @Override
    public List<BaoxiaoBill> findmyBaoxiaoBillList(long id) {
        BaoxiaoBillExample baoxiaoBillExample = new BaoxiaoBillExample();
        BaoxiaoBillExample.Criteria criteria = baoxiaoBillExample.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<BaoxiaoBill> list = baoxiaoBillMapper.selectByExample(baoxiaoBillExample);
        return list;
    }

    @Override
    public BaoxiaoBill findBaoxiaoBillById(Long id) {

        return baoxiaoBillMapper.selectByPrimaryKey(id);
    }

    @Override
    public void deleteBaoxiaoBillById(long l) {
        baoxiaoBillMapper.deleteByPrimaryKey(l);
    }
}
