package com.kjd.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjd.backend.entity.Reimbursement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReimbursementMapper extends BaseMapper<Reimbursement> {

    List<Reimbursement> selectWithDetails(@Param("id") Long id);

    IPage<Reimbursement> selectPageList(Page<Reimbursement> page,
                                        @Param("reimbursementNo") String reimbursementNo,
                                        @Param("reimbursementTitle") String reimbursementTitle,
                                        @Param("businessTripReason") String businessTripReason,
                                        @Param("reimCompanyId") String reimCompanyId,
                                        @Param("reimDepartmentId") String reimDepartmentId,
                                        @Param("reimburserId") String reimburserId,
                                        @Param("businessTypeId") String businessTypeId);

    int updateStatusById(@Param("id") Long id, @Param("status") Integer status);
}
