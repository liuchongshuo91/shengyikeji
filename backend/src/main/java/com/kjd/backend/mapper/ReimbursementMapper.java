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

    /**
     * 根据主表ID查询行程列表
     */
    List<Reimbursement> selectWithDetails(@Param("id") String id);

    /**
     * 分页查询（带条件）— 由 MyBatis Plus 分页插件自动处理分页和 count
     */
    IPage<Reimbursement> selectPageList(Page<Reimbursement> page,
                                        @Param("reimbursementNo") String reimbursementNo,
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
