package com.kjd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kjd.backend.entity.CalendarDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarDayMapper extends BaseMapper<CalendarDay> {

    /**
     * 根据补助ID查询日历明细
     */
    List<CalendarDay> selectBySubsidyId(@Param("subsidyId") String subsidyId);

    /**
     * 根据补助ID删除日历明细
     */
    int deleteBySubsidyId(@Param("subsidyId") String subsidyId);

    /**
     * 根据主表ID删除日历明细（通过补助表关联）
     */
    int deleteByMainId(@Param("mainId") String mainId);
}
