package com.neusoft.nep.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nep.dto.AqiFeedbackQueryDTO;
import com.neusoft.nep.dto.AqiFeedbackSubmitDTO;
import com.neusoft.nep.dto.AqiFeedbackUpdateDTO;
import com.neusoft.nep.entity.AqiFeedback;
import com.neusoft.nep.vo.AqiFeedbackVO;

import java.util.List;

/**
 * 公众监督反馈信息服务接口
 */
public interface AqiFeedbackService {

    /**
     * 提交反馈信息
     */
    boolean submitFeedback(AqiFeedbackSubmitDTO dto);

    /**
     * 修改反馈信息
     */
    boolean updateFeedback(AqiFeedbackUpdateDTO dto);

    /**
     * 删除反馈信息
     */
    boolean deleteFeedback(Integer afId, String telId);

    /**
     * 分页查询反馈列表
     */
    Page<AqiFeedbackVO> queryFeedbackList(AqiFeedbackQueryDTO queryDTO);

    /**
     * 根据ID查询反馈详情
     */
    AqiFeedbackVO getFeedbackDetail(Integer afId);

    /**
     * 根据监督员手机号查询历史反馈
     */
    List<AqiFeedbackVO> getHistoryByTelId(String telId);

    /**
     * 指派网格员
     */
    boolean assignGridMember(Integer afId, String gmId);

    /**
     * 获取未指派的反馈列表
     */
    List<AqiFeedbackVO> getUnassignedList();

    /**
     * 根据网格员ID获取已指派的任务列表
     */
    List<AqiFeedbackVO> getTasksByGridMember(String gmId);

    /**
     * 确认反馈任务（由网格员提交实测数据后）
     */
    boolean confirmFeedback(Integer afId);

    /**
     * 转换为视图对象
     */
    AqiFeedbackVO convertToVO(AqiFeedback feedback);
}