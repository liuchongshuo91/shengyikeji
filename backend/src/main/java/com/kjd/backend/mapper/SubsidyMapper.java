package com.kjd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kjd.backend.entity.Subsidy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubsidyMapper extends BaseMapper<Subsidy> {

    List<Subsidy> selectByMainId(@Param("mainId") Long mainId);

    int deleteByMainId(@Param("mainId") Long mainId);
}
