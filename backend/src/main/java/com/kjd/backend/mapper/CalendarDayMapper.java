package com.kjd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kjd.backend.entity.CalendarDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarDayMapper extends BaseMapper<CalendarDay> {

    List<CalendarDay> selectBySubsidyId(@Param("subsidyId") Long subsidyId);

    int deleteBySubsidyId(@Param("subsidyId") Long subsidyId);

    int deleteByMainId(@Param("mainId") Long mainId);
}
