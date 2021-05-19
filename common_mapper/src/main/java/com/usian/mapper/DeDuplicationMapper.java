package com.usian.mapper;

import com.usian.pojo.DeDuplication;
import com.usian.pojo.DeDuplicationExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeDuplicationMapper {
    int countByExample(DeDuplicationExample example);

    int deleteByExample(DeDuplicationExample example);

    int deleteByPrimaryKey(String txNo);

    int insert(DeDuplication record);

    int insertSelective(DeDuplication record);

    List<DeDuplication> selectByExample(DeDuplicationExample example);

    DeDuplication selectByPrimaryKey(String txNo);

    int updateByExampleSelective(@Param("record") DeDuplication record, @Param("example") DeDuplicationExample example);

    int updateByExample(@Param("record") DeDuplication record, @Param("example") DeDuplicationExample example);

    int updateByPrimaryKeySelective(DeDuplication record);

    int updateByPrimaryKey(DeDuplication record);
}