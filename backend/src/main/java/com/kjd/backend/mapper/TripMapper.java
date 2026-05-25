package com.kjd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kjd.backend.entity.Trip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TripMapper extends BaseMapper<Trip> {

    /**
     * 根据主表ID查询行程列表
     */
    List<Trip> selectByMainId(@Param("mainId") String mainId);

    /**
     * 根据主表ID删除行程
     */
    int deleteByMainId(@Param("mainId") String mainId);
}
