package com.usian.service;

import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.LocalMessageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocalMessageService {

    @Autowired
    private LocalMessageMapper localMessageMapper;

    public List<LocalMessage> selectlocalMessageByStatus(Integer status) {
        LocalMessageExample localMessageExample = new LocalMessageExample();
        LocalMessageExample.Criteria criteria = localMessageExample.createCriteria();
        criteria.andStateEqualTo(status);
        return localMessageMapper.selectByExample(localMessageExample);
    }
}