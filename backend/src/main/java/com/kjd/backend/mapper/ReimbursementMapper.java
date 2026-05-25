package com.kjd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kjd.backend.entity.Reimbursement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReimbursementMapper extends BaseMapper<Reimbursement> {

    /**
     * 根据主表ID查询行程列表
     */
    List<Reimbursement> selectWithDetails(@Param("id") String id);

    /**
     * 分页查询（带条件）
     */
    List<Reimbursement> selectPageList(@Param("reimbursementNo") String reimbursementNo,
                                       @Param("reimbursementTitle") String reimbursementTitle,
                                       @Param("businessTripReason") String businessTripReason,
                                       @Param("reimCompanyId") String reimCompanyId,
                                       @Param("reimDepartmentId") String reimDepartmentId,
                                       @Param("reimburserId") String reimburserId,
                                       @Param("businessTypeId") String businessTypeId);

    /**
     * 统计总数（带条件）
     */
    Long selectCountWithCondition(@Param("reimbursementNo") String reimbursementNo,
                                  @Param("reimbursementTitle") String reimbursementTitle,
                                  @Param("businessTripReason") String businessTripReason,
                                  @Param("reimCompanyId") String reimCompanyId,
                                  @Param("reimDepartmentId") String reimDepartmentId,
                                  @Param("reimburserId") String reimburserId,
                                  @Param("businessTypeId") String businessTypeId);

    /**
     * 更新状态
     */
    int updateStatusById(@Param("id") String id, @Param("status") Integer status);
}
