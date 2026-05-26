package com.kjd.backend.controller;

import com.kjd.backend.vo.Result;
import com.kjd.backend.dto.ReimbursementQueryDTO;
import com.kjd.backend.entity.Reimbursement;
import com.kjd.backend.service.DictionaryService;
import com.kjd.backend.service.ReimbursementService;
import com.kjd.backend.vo.DictionaryItemVO;
import com.kjd.backend.vo.PageResultVO;
import com.kjd.backend.vo.ReimbursementVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReimbursementController {
    private final ReimbursementService reimbursementService;
    private final DictionaryService dictionaryService;

    public ReimbursementController(ReimbursementService reimbursementService, DictionaryService dictionaryService) {
        this.reimbursementService = reimbursementService;
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/dict")
    public Result<Map<String, List<DictionaryItemVO>>> dictionaries() {
        return Result.success(dictionaryService.all());
    }

    @PostMapping("/reimbursements/page")
    public Result<PageResultVO<ReimbursementVO>> page(@RequestBody ReimbursementQueryDTO query) {
        return Result.success(reimbursementService.page(query));
    }

    @GetMapping("/reimbursements/{id}")
    public Result<ReimbursementVO> detail(@PathVariable Long id) {
        ReimbursementVO vo = reimbursementService.find(id);
        if (vo == null) return Result.fail("单据不存在");
        return Result.success(vo);
    }

    @PostMapping("/reimbursements/save")
    public Result<ReimbursementVO> save(@RequestBody Reimbursement item) {
        return Result.success(reimbursementService.save(item, false));
    }

    @PostMapping("/reimbursements/submit")
    public Result<ReimbursementVO> submit(@RequestBody Reimbursement item) {
        return Result.success(reimbursementService.save(item, true));
    }

    @PostMapping("/reimbursements/{id}/void")
    public Result<Void> voidDocument(@PathVariable Long id) {
        reimbursementService.voidDocument(id);
        return Result.success(null);
    }

    @PostMapping("/reimbursements/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        reimbursementService.delete(id);
        return Result.success(null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> validation(Exception e) {
        return Result.fail(e.getMessage());
    }
}
