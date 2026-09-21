package com.neusoft.nep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neusoft.nep.dto.AqiFeedbackQueryDTO;
import com.neusoft.nep.dto.AqiFeedbackSubmitDTO;
import com.neusoft.nep.dto.AqiFeedbackUpdateDTO;
import com.neusoft.nep.entity.*;
import com.neusoft.nep.enums.AqiGradeEnum;
import com.neusoft.nep.enums.FeedbackStateEnum;
import com.neusoft.nep.mapper.*;
import com.neusoft.nep.service.AqiFeedbackService;
import com.neusoft.nep.service.AqiService;
import com.neusoft.nep.vo.AqiFeedbackVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AqiFeedbackServiceImpl implements AqiFeedbackService {

    @Autowired
    private AqiFeedbackMapper aqiFeedbackMapper;

    @Autowired
    private AqiService aqiService;

    @Autowired
    private GridProvinceMapper gridProvinceMapper;

    @Autowired
    private GridCityMapper gridCityMapper;

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitFeedback(AqiFeedbackSubmitDTO dto) {
        if (dto.getEstimatedGrade() < 1 || dto.getEstimatedGrade() > 6) {
            throw new IllegalArgumentException("预估AQI等级必须在1-6之间");
        }

        AqiFeedback feedback = new AqiFeedback();
        BeanUtils.copyProperties(dto, feedback);

        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();
        feedback.setAfDate(now.format(DATE_FORMATTER));
        feedback.setAfTime(time.format(TIME_FORMATTER));

        feedback.setState(FeedbackStateEnum.UNASSIGNED.getCode());
        feedback.setGmId("0");

        int result = aqiFeedbackMapper.insert(feedback);
        return result > 0;
    }

    /**
     * 修改反馈信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFeedback(AqiFeedbackUpdateDTO dto) {
        // 检查反馈是否存在
        AqiFeedback feedback = aqiFeedbackMapper.selectById(dto.getAfId());
        if (feedback == null) {
            throw new IllegalArgumentException("反馈记录不存在");
        }
        // 校验归属
        if (!feedback.getTelId().equals(dto.getTelId())) {
            throw new IllegalArgumentException("无权修改他人的反馈信息");
        }
        // 仅未指派状态可修改
        if (feedback.getState() != FeedbackStateEnum.UNASSIGNED.getCode()) {
            throw new IllegalStateException("该反馈已被指派或确认，无法修改");
        }
        // 校验等级
        if (dto.getEstimatedGrade() < 1 || dto.getEstimatedGrade() > 6) {
            throw new IllegalArgumentException("预估AQI等级必须在1-6之间");
        }

        int result = aqiFeedbackMapper.updateFeedback(
                dto.getAfId(),
                dto.getTelId(),
                dto.getProvinceId(),
                dto.getCityId(),
                dto.getAddress(),
                dto.getInformation(),
                dto.getEstimatedGrade()
        );
        return result > 0;
    }

    /**
     * 删除反馈信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFeedback(Integer afId, String telId) {
        if (afId == null || !StringUtils.hasText(telId)) {
            return false;
        }

        AqiFeedback feedback = aqiFeedbackMapper.selectById(afId);
        if (feedback == null) {
            throw new IllegalArgumentException("反馈记录不存在");
        }
        if (!feedback.getTelId().equals(telId)) {
            throw new IllegalArgumentException("无权删除他人的反馈信息");
        }
        if (feedback.getState() != FeedbackStateEnum.UNASSIGNED.getCode()) {
            throw new IllegalStateException("该反馈已被指派或确认，无法删除");
        }

        int result = aqiFeedbackMapper.deleteFeedback(afId, telId);
        return result > 0;
    }

    @Override
    public Page<AqiFeedbackVO> queryFeedbackList(AqiFeedbackQueryDTO queryDTO) {
        QueryWrapper<AqiFeedback> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getTelId())) {
            wrapper.eq("tel_id", queryDTO.getTelId());
        }
        if (queryDTO.getProvinceId() != null) {
            wrapper.eq("province_id", queryDTO.getProvinceId());
        }
        if (queryDTO.getCityId() != null) {
            wrapper.eq("city_id", queryDTO.getCityId());
        }
        // ========== 修改：默认只查询未指派状态 ==========
        if (queryDTO.getState() != null) {
            wrapper.eq("state", queryDTO.getState());
        } else {
            // 未传状态时，默认查询未指派
            wrapper.eq("state", 0);
        }
        if (StringUtils.hasText(queryDTO.getGmId())) {
            wrapper.eq("gm_id", queryDTO.getGmId());
        }
        if (StringUtils.hasText(queryDTO.getStartDate())) {
            wrapper.ge("af_date", queryDTO.getStartDate());
        }
        if (StringUtils.hasText(queryDTO.getEndDate())) {
            wrapper.le("af_date", queryDTO.getEndDate());
        }

        wrapper.orderByAsc("af_id");

        Page<AqiFeedback> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<AqiFeedback> feedbackPage = aqiFeedbackMapper.selectPage(page, wrapper);

        Page<AqiFeedbackVO> voPage = new Page<>(feedbackPage.getCurrent(), feedbackPage.getSize(), feedbackPage.getTotal());
        List<AqiFeedbackVO> voList = feedbackPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public AqiFeedbackVO getFeedbackDetail(Integer afId) {
        if (afId == null) {
            return null;
        }
        AqiFeedback feedback = aqiFeedbackMapper.selectById(afId);
        return convertToVO(feedback);
    }

    @Override
    public List<AqiFeedbackVO> getHistoryByTelId(String telId) {
        if (!StringUtils.hasText(telId)) {
            return new ArrayList<>();
        }
        List<AqiFeedback> feedbackList = aqiFeedbackMapper.selectByTelId(telId);
        return feedbackList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignGridMember(Integer afId, String gmId) {
        if (afId == null || !StringUtils.hasText(gmId)) {
            return false;
        }

        AqiFeedback feedback = aqiFeedbackMapper.selectById(afId);
        if (feedback == null) {
            throw new IllegalArgumentException("反馈记录不存在");
        }
        if (feedback.getState() != FeedbackStateEnum.UNASSIGNED.getCode()) {
            throw new IllegalStateException("该反馈已指派或已确认，不能重复指派");
        }

        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();

        int result = aqiFeedbackMapper.updateStateAndAssign(
                afId,
                FeedbackStateEnum.ASSIGNED.getCode(),
                gmId,
                now.format(DATE_FORMATTER),
                time.format(TIME_FORMATTER)
        );
        return result > 0;
    }

    @Override
    public List<AqiFeedbackVO> getUnassignedList() {
        List<AqiFeedback> feedbackList = aqiFeedbackMapper.selectByState(FeedbackStateEnum.UNASSIGNED.getCode());
        return feedbackList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AqiFeedbackVO> getTasksByGridMember(String gmId) {
        if (!StringUtils.hasText(gmId)) {
            return new ArrayList<>();
        }
        // 查询 state = 1（已指派）或 state = 2（已确认）的任务
        List<AqiFeedback> feedbackList = aqiFeedbackMapper.selectByGridMember(gmId);

        System.out.println("网格员 " + gmId + " 的任务列表:");
        feedbackList.forEach(f -> {
            System.out.println("  - 任务ID: " + f.getAfId() + ", 状态: " + f.getState());
        });

        return feedbackList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmFeedback(Integer afId) {
        if (afId == null) {
            return false;
        }

        AqiFeedback feedback = aqiFeedbackMapper.selectById(afId);
        if (feedback == null) {
            throw new IllegalArgumentException("反馈记录不存在");
        }
        if (feedback.getState() != FeedbackStateEnum.ASSIGNED.getCode()) {
            throw new IllegalStateException("当前状态不允许确认，只有已指派的任务才能确认");
        }

        AqiFeedback update = new AqiFeedback();
        update.setAfId(afId);
        update.setState(FeedbackStateEnum.CONFIRMED.getCode());

        int result = aqiFeedbackMapper.updateById(update);
        System.out.println("确认反馈: afId=" + afId + ", 结果=" + (result > 0 ? "成功" : "失败"));
        return result > 0;
    }

    @Override
    public AqiFeedbackVO convertToVO(AqiFeedback feedback) {
        if (feedback == null) {
            return null;
        }

        AqiFeedbackVO vo = new AqiFeedbackVO();
        BeanUtils.copyProperties(feedback, vo);

        // 设置状态描述
        FeedbackStateEnum stateEnum = FeedbackStateEnum.getByCode(feedback.getState());
        if (stateEnum != null) {
            vo.setStateDesc(stateEnum.getDesc());
        }

        // 设置预估等级描述
        AqiGradeEnum gradeEnum = AqiGradeEnum.getByCode(feedback.getEstimatedGrade());
        if (gradeEnum != null) {
            vo.setEstimatedGradeDesc(gradeEnum.getAqiExplain());
        }

        // ========== 新增：查询监督员姓名和电话 ==========
        if (StringUtils.hasText(feedback.getTelId())) {
            try {
                Supervisor supervisor = supervisorMapper.selectByTelId(feedback.getTelId());
                if (supervisor != null) {
                    vo.setSupervisorName(supervisor.getRealName());
                    vo.setSupervisorTel(supervisor.getTelId());
                    vo.setSupervisorSex(supervisor.getSex());
                    vo.setSupervisorBirthday(supervisor.getBirthday());

                    // 性别描述
                    if (supervisor.getSex() != null) {
                        vo.setSupervisorSexDesc(supervisor.getSex() == 1 ? "男" : "女");
                    }
                } else {
                    vo.setSupervisorName("未知用户");
                    vo.setSupervisorTel(feedback.getTelId());
                }
            } catch (Exception e) {
                System.err.println("查询监督员信息失败: " + e.getMessage());
                vo.setSupervisorName("未知用户");
                vo.setSupervisorTel(feedback.getTelId());
            }
        }

        // 查询省份名称
        if (feedback.getProvinceId() != null) {
            try {
                GridProvince province = gridProvinceMapper.selectById(feedback.getProvinceId());
                if (province != null) {
                    vo.setProvinceName(province.getProvinceName());
                }
            } catch (Exception e) {
                System.err.println("查询省份失败: " + e.getMessage());
            }
        }

        // 查询城市名称
        if (feedback.getCityId() != null) {
            try {
                GridCity city = gridCityMapper.selectById(feedback.getCityId());
                if (city != null) {
                    vo.setCityName(city.getCityName());
                }
            } catch (Exception e) {
                System.err.println("查询城市失败: " + e.getMessage());
            }
        }

        // ========== 新增：查询实测AQI数据（仅当状态为已确认时） ==========
        if (feedback.getState() != null && feedback.getState() == 2) {
            try {
                Statistics statistics = statisticsMapper.selectByAfId(feedback.getAfId());
                if (statistics != null) {
                    // 实测AQI等级
                    vo.setMeasuredGrade(statistics.getAqiId());
                    AqiGradeEnum measuredGradeEnum = AqiGradeEnum.getByCode(statistics.getAqiId());
                    if (measuredGradeEnum != null) {
                        vo.setMeasuredGradeDesc(measuredGradeEnum.getAqiExplain());
                        vo.setMeasuredGradeColor(measuredGradeEnum.getColor());
                    }

                    // 实测污染物数据
                    vo.setSo2Value(statistics.getSo2Value());
                    vo.setSo2Level(statistics.getSo2Level());
                    if (statistics.getSo2Level() != null) {
                        AqiGradeEnum so2Grade = AqiGradeEnum.getByCode(statistics.getSo2Level());
                        if (so2Grade != null) {
                            vo.setSo2LevelDesc(so2Grade.getAqiExplain());
                            vo.setSo2Color(so2Grade.getColor());
                        }
                    }
                    vo.setCoValue(statistics.getCoValue());
                    vo.setCoLevel(statistics.getCoLevel());
                    if (statistics.getCoLevel() != null) {
                        AqiGradeEnum coGrade = AqiGradeEnum.getByCode(statistics.getCoLevel());
                        if (coGrade != null) {
                            vo.setCoLevelDesc(coGrade.getAqiExplain());
                            vo.setCoColor(coGrade.getColor());
                        }
                    }
                    vo.setSpmValue(statistics.getSpmValue());
                    vo.setSpmLevel(statistics.getSpmLevel());
                    if (statistics.getSpmLevel() != null) {
                        AqiGradeEnum spmGrade = AqiGradeEnum.getByCode(statistics.getSpmLevel());
                        if (spmGrade != null) {
                            vo.setSpmLevelDesc(spmGrade.getAqiExplain());
                            vo.setSpmColor(spmGrade.getColor());
                        }
                    }

                    // 实测确认时间
                    vo.setConfirmDate(statistics.getConfirmDate());
                    vo.setConfirmTime(statistics.getConfirmTime());
                }
            } catch (Exception e) {
                System.err.println("查询实测AQI数据失败: " + e.getMessage());
            }
        }

        return vo;
    }
}