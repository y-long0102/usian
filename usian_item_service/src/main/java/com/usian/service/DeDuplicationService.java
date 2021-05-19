package com.usian.service;

import com.usian.mapper.DeDuplicationMapper;
import com.usian.pojo.DeDuplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class DeDuplicationService {

    @Autowired
    private DeDuplicationMapper deDuplicationMapper;

    public DeDuplication selectDeDuplicationByTxNo(String txNo) {
        return deDuplicationMapper.selectByPrimaryKey(txNo);
    }

    public void insertDeDuplication(String txNo) {
        DeDuplication deDuplication = new DeDuplication();
        deDuplication.setTxNo(txNo);
        deDuplication.setCreateTime(new Date());
        deDuplicationMapper.insertSelective(deDuplication);
    }
}