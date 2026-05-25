package com.kjd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kjd.backend.entity.Subsidy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubsidyMapper extends BaseMapper<Subsidy> {

    /**
     * 根据主表ID查询补助列表
     */
    List<Subsidy> selectByMainId(@Param("mainId") String mainId);

    /**
     * 根据主表ID删除补助
     */
    int deleteByMainId(@Param("mainId") String mainId);
}
