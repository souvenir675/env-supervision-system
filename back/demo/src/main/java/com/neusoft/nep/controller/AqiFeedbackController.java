package com.neusoft.nep.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nep.common.Result;
import com.neusoft.nep.dto.AqiFeedbackQueryDTO;
import com.neusoft.nep.dto.AqiFeedbackSubmitDTO;
import com.neusoft.nep.dto.AqiFeedbackUpdateDTO;
import com.neusoft.nep.service.AqiFeedbackService;
import com.neusoft.nep.vo.AqiFeedbackVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * 公众监督反馈控制器
 */
@RestController
@RequestMapping("/api/feedback")
@Validated
public class AqiFeedbackController {

    @Autowired
    private AqiFeedbackService aqiFeedbackService;

    /**
     * 公众监督员提交反馈
     */
    @PostMapping("/submit")
    public Result<Boolean> submitFeedback(@Valid @RequestBody AqiFeedbackSubmitDTO dto) {
        try {
            boolean result = aqiFeedbackService.submitFeedback(dto);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改反馈信息
     */
    @PutMapping("/update")
    public Result<Boolean> updateFeedback(@Valid @RequestBody AqiFeedbackUpdateDTO dto) {
        try {
            boolean result = aqiFeedbackService.updateFeedback(dto);
            return Result.success(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除反馈信息
     */
    @DeleteMapping("/delete/{afId}")
    public Result<Boolean> deleteFeedback(@PathVariable Integer afId, @RequestParam String telId) {
        try {
            boolean result = aqiFeedbackService.deleteFeedback(afId, telId);
            return Result.success(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询反馈列表（管理员）
     */
    @PostMapping("/list")
    public Result<Page<AqiFeedbackVO>> queryList(@RequestBody AqiFeedbackQueryDTO queryDTO) {
        Page<AqiFeedbackVO> page = aqiFeedbackService.queryFeedbackList(queryDTO);
        return Result.success(page);
    }

    /**
     * 查询反馈详情
     */
    @GetMapping("/detail/{afId}")
    public Result<AqiFeedbackVO> getDetail(@PathVariable Integer afId) {
        AqiFeedbackVO vo = aqiFeedbackService.getFeedbackDetail(afId);
        if (vo == null) {
            return Result.error("反馈记录不存在");
        }
        return Result.success(vo);
    }

    /**
     * 查询公众监督员的历史反馈
     */
    @GetMapping("/history/{telId}")
    public Result<List<AqiFeedbackVO>> getHistory(@PathVariable String telId) {
        List<AqiFeedbackVO> list = aqiFeedbackService.getHistoryByTelId(telId);
        return Result.success(list);
    }

    /**
     * 获取未指派的反馈列表
     */
    @GetMapping("/unassigned")
    public Result<List<AqiFeedbackVO>> getUnassignedList() {
        List<AqiFeedbackVO> list = aqiFeedbackService.getUnassignedList();
        return Result.success(list);
    }

    /**
     * 管理员指派网格员
     */
    @PostMapping("/assign")
    public Result<Boolean> assignGridMember(
            @RequestParam Integer afId,
            @RequestParam String gmId) {
        try {
            boolean result = aqiFeedbackService.assignGridMember(afId, gmId);
            return Result.success(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取网格员的任务列表
     */
    @GetMapping("/tasks/{gmId}")
    public Result<List<AqiFeedbackVO>> getTasksByGridMember(@PathVariable String gmId) {
        List<AqiFeedbackVO> list = aqiFeedbackService.getTasksByGridMember(gmId);
        return Result.success(list);
    }

    /**
     * 确认反馈任务（网格员提交实测数据后）
     */
    @PostMapping("/confirm/{afId}")
    public Result<Boolean> confirmFeedback(@PathVariable Integer afId) {
        try {
            boolean result = aqiFeedbackService.confirmFeedback(afId);
            return Result.success(result);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }
}