package com.web.oa.service;


import com.web.oa.pojo.BaoxiaoBill;

import java.util.List;

public interface BaoxiaoService {

    void saveBaoxiao(BaoxiaoBill baoxiaoBill);

    List<BaoxiaoBill> findmyBaoxiaoBillList(long id);

    BaoxiaoBill findBaoxiaoBillById(Long id);

    void deleteBaoxiaoBillById(long l);
}
