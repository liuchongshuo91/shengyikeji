package com.kjd.backend.service;

import com.kjd.backend.dto.ReimbursementQueryDTO;
import com.kjd.backend.entity.Reimbursement;
import com.kjd.backend.vo.PageResultVO;
import com.kjd.backend.vo.ReimbursementVO;

public interface ReimbursementService {
    PageResultVO<ReimbursementVO> page(ReimbursementQueryDTO query);

    ReimbursementVO find(Long id);

    ReimbursementVO save(Reimbursement item, boolean submit);

    void voidDocument(Long id);

    void delete(Long id);
}
