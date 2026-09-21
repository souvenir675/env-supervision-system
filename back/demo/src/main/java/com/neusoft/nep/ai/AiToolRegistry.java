package com.neusoft.nep.ai;

import com.neusoft.nep.dto.AqiFeedbackSubmitDTO;
import com.neusoft.nep.dto.GridMemberAssignDTO;
import com.neusoft.nep.dto.StatisticsQueryDTO;
import com.neusoft.nep.entity.GridMember;
import com.neusoft.nep.service.*;
import com.neusoft.nep.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * AI 工具注册中心
 * 定义所有可被 AI Agent 调用的 Java 方法
 */
@Component
public class AiToolRegistry {

    @Autowired
    private AqiFeedbackService aqiFeedbackService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private GridMemberService gridMemberService;

    @Autowired
    private AqiService aqiService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 获取所有工具定义（供 DeepSeek 使用）
     */
    public List<Map<String, Object>> getTools(String userType) {
        List<Map<String, Object>> tools = new ArrayList<>();

        // 监督员可用工具
        if ("supervisor".equals(userType)) {
            tools.add(buildTool(
                    "submit_feedback",
                    "提交空气质量监督反馈信息。当用户表达要提交反馈、上报空气质量时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "telId", Map.of("type", "string", "description", "监督员手机号"),
                                    "provinceName", Map.of("type", "string", "description", "省份名称，如：河北省"),
                                    "cityName", Map.of("type", "string", "description", "城市名称，如：保定市"),
                                    "address", Map.of("type", "string", "description", "详细地址"),
                                    "estimatedGrade", Map.of("type", "integer", "description", "预估AQI等级 1-6，1优 2良 3轻度污染 4中度污染 5重度污染 6严重污染"),
                                    "information", Map.of("type", "string", "description", "空气质量描述")
                            ),
                            "required", List.of("telId", "provinceName", "cityName", "address", "estimatedGrade", "information")
                    )
            ));

            tools.add(buildTool(
                    "query_my_history",
                    "查询当前监督员的历史反馈记录。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "telId", Map.of("type", "string", "description", "监督员手机号")
                            ),
                            "required", List.of("telId")
                    )
            ));
        }

        // 网格员可用工具
        if ("grid".equals(userType)) {
            tools.add(buildTool(
                    "submit_aqi_data",
                    "网格员提交实测AQI数据。当用户说录入数据、提交检测结果时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "afId", Map.of("type", "integer", "description", "反馈任务编号"),
                                    "so2Value", Map.of("type", "integer", "description", "SO₂实测浓度 ug/m³"),
                                    "coValue", Map.of("type", "integer", "description", "CO实测浓度 ug/m³"),
                                    "spmValue", Map.of("type", "integer", "description", "PM2.5实测浓度 ug/m³"),
                                    "gmId", Map.of("type", "string", "description", "网格员手机号")
                            ),
                            "required", List.of("afId", "so2Value", "coValue", "spmValue", "gmId")
                    )
            ));

            tools.add(buildTool(
                    "query_my_tasks",
                    "查询当前网格员的任务列表。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "gmId", Map.of("type", "string", "description", "网格员手机号")
                            ),
                            "required", List.of("gmId")
                    )
            ));
        }

        // 管理员可用工具
        if ("admin".equals(userType)) {
            tools.add(buildTool(
                    "smart_assign_grid_member",
                    "智能指派网格员处理反馈任务。系统会优先选择与反馈任务同地区的可工作网格员，" +
                            "如果本地没有，则就近选择其他地区的网格员。" +
                            "当用户说'把反馈X指派给网格员'或'处理反馈X'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "afId", Map.of("type", "integer", "description", "反馈任务编号"),
                                    "preferredGmId", Map.of("type", "string", "description", "用户指定的网格员编号（可选），如果用户没指定具体网格员，则不传此参数")
                            ),
                            "required", List.of("afId")
                    )
            ));

            tools.add(buildTool(
                    "query_unassigned_feedbacks",
                    "查询所有未指派的反馈任务。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));

            tools.add(buildTool(
                    "query_available_grid_members",
                    "查询所有可工作的网格员列表。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));

            tools.add(buildTool(
                    "get_province_statistics",
                    "获取按省分组的AQI超标统计。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));

            tools.add(buildTool(
                    "get_aqi_distribution",
                    "获取AQI等级分布统计。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));

            tools.add(buildTool(
                    "get_realtime_statistics",
                    "获取实时检测统计数据（检测总量、良好、超标）。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));

            tools.add(buildTool(
                    "get_coverage_statistics",
                    "获取全国网格覆盖率。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));
        }

        // 决策者可用工具
        if ("decision".equals(userType)) {
            // 工具 1：查询指定省份指定污染物的超标统计
            tools.add(buildTool(
                    "query_province_pollutant_statistics",
                    "查询某个省份某段时间内某项污染物的超标累计数量。" +
                            "当用户询问'某省某污染物超标多少'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "provinceName", Map.of("type", "string", "description", "省份名称，如：河北省"),
                                    "pollutant", Map.of("type", "string", "description", "污染物类型：pm25（PM2.5）、so2（二氧化硫）、co（一氧化碳）、aqi（综合）"),
                                    "startDate", Map.of("type", "string", "description", "开始日期 YYYY-MM-DD，可选"),
                                    "endDate", Map.of("type", "string", "description", "结束日期 YYYY-MM-DD，可选")
                            ),
                            "required", List.of("provinceName", "pollutant")
                    )
            ));

            // 工具 2：查询所有省份超标排名
            tools.add(buildTool(
                    "query_pollutant_ranking",
                    "查询某项污染物超标最严重的省份排名。当用户问'哪个省某污染物超标最严重'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "pollutant", Map.of("type", "string", "description", "污染物类型：pm25、so2、co、aqi"),
                                    "topN", Map.of("type", "integer", "description", "返回前 N 名，默认 5")
                            ),
                            "required", List.of("pollutant")
                    )
            ));

            // 工具 3：查询 AQI 趋势
            tools.add(buildTool(
                    "query_aqi_trend",
                    "查询指定省份或全国的 AQI 超标月度趋势。当用户问'趋势'、'走势'、'某月数据'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "provinceName", Map.of("type", "string", "description", "省份名称，可选；为空表示全国"),
                                    "months", Map.of("type", "integer", "description", "查询最近几个月，默认 12")
                            ),
                            "required", List.of()
                    )
            ));

            // 工具 4：对比两个省份
            tools.add(buildTool(
                    "compare_provinces",
                    "对比两个省份某项污染物的超标情况。当用户问'对比 A 和 B 省'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "provinceA", Map.of("type", "string", "description", "省份A名称"),
                                    "provinceB", Map.of("type", "string", "description", "省份B名称"),
                                    "pollutant", Map.of("type", "string", "description", "污染物类型：pm25、so2、co、aqi")
                            ),
                            "required", List.of("provinceA", "provinceB", "pollutant")
                    )
            ));

            // 工具 5：对比两个时间段
            tools.add(buildTool(
                    "compare_time_periods",
                    "对比同一个省份或全国在两个时间段的 AQI 超标情况。当用户问'对比某月和某月'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "provinceName", Map.of("type", "string", "description", "省份名称，可选；为空表示全国"),
                                    "period1Start", Map.of("type", "string", "description", "时间段1开始日期 YYYY-MM-DD"),
                                    "period1End", Map.of("type", "string", "description", "时间段1结束日期 YYYY-MM-DD"),
                                    "period2Start", Map.of("type", "string", "description", "时间段2开始日期 YYYY-MM-DD"),
                                    "period2End", Map.of("type", "string", "description", "时间段2结束日期 YYYY-MM-DD")
                            ),
                            "required", List.of("period1Start", "period1End", "period2Start", "period2End")
                    )
            ));

            // 工具 6：检测异常
            tools.add(buildTool(
                    "detect_anomaly",
                    "检测某项污染物在各省或各月份的异常数据（明显偏离历史规律）。" +
                            "当用户问'异常'、'飙升'、'突变'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "pollutant", Map.of("type", "string", "description", "污染物类型：pm25、so2、co、aqi")
                            ),
                            "required", List.of("pollutant")
                    )
            ));

            // 工具 7：全省覆盖率
            tools.add(buildTool(
                    "get_coverage_statistics",
                    "查询全国省份和城市的网格覆盖率。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));

            // 工具 8：实时统计
            tools.add(buildTool(
                    "get_realtime_statistics",
                    "查询实时检测统计数据（总量、良好、超标）。",
                    Map.of("type", "object", "properties", new HashMap<>())
            ));

            // 工具 9：查询某省所有城市的超标情况
            tools.add(buildTool(
                    "query_cities_by_province",
                    "查询某个省份下辖各城市的 AQI 超标分布。当用户问'某省各市情况'时调用。",
                    Map.of(
                            "type", "object",
                            "properties", Map.of(
                                    "provinceName", Map.of("type", "string", "description", "省份名称")
                            ),
                            "required", List.of("provinceName")
                    )
            ));
        }

        return tools;
    }

    private Map<String, Object> buildTool(String name, String description, Map<String, Object> parameters) {
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");
        Map<String, Object> function = new HashMap<>();
        function.put("name", name);
        function.put("description", description);
        function.put("parameters", parameters);
        tool.put("function", function);
        return tool;
    }

    /**
     * 执行工具
     */
    public Object executeTool(String toolName, Map<String, Object> args) {
        System.out.println("执行工具: " + toolName + ", 参数: " + args);

        try {
            switch (toolName) {
                // ========== 监督员 ==========
                case "submit_feedback":
                    return handleSubmitFeedback(args);
                case "query_my_history":
                    return aqiFeedbackService.getHistoryByTelId((String) args.get("telId"));

                // ========== 网格员 ==========
                case "submit_aqi_data":
                    return handleSubmitAqiData(args);
                case "query_my_tasks":
                    return aqiFeedbackService.getTasksByGridMember((String) args.get("gmId"));

                // ========== 管理员 ==========
                case "smart_assign_grid_member":
                    return handleSmartAssign(args);
                case "query_unassigned_feedbacks":
                    return aqiFeedbackService.getUnassignedList();
                case "query_available_grid_members":
                    return gridMemberService.findAllAvailable();
                case "get_province_statistics":
                    return statisticsService.getProvinceStatistics(null);
                case "get_aqi_distribution":
                    return statisticsService.getAqiDistribution();
                case "get_realtime_statistics":
                    return statisticsService.getRealTimeStatistics();
                case "get_coverage_statistics":
                    return statisticsService.getCoverageStatistics();

                // ========== 决策者 ==========
                case "query_province_pollutant_statistics":
                    return handleQueryProvincePollutant(args);
                case "query_pollutant_ranking":
                    return handleQueryPollutantRanking(args);
                case "query_aqi_trend":
                    return handleQueryAqiTrend(args);
                case "compare_provinces":
                    return handleCompareProvinces(args);
                case "compare_time_periods":
                    return handleCompareTimePeriods(args);
                case "detect_anomaly":
                    return handleDetectAnomaly(args);
                case "query_cities_by_province":
                    return handleQueryCitiesByProvince(args);

                default:
                    return Map.of("error", "未知工具: " + toolName);
            }
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    // ========== 工具实现 ==========

    /**
     * 提交反馈：通过省份/城市名称自动查询ID
     */
    private Object handleSubmitFeedback(Map<String, Object> args) {
        // 根据名称查询 provinceId 和 cityId
        Integer provinceId = findProvinceIdByName((String) args.get("provinceName"));
        Integer cityId = findCityIdByName(provinceId, (String) args.get("cityName"));

        if (provinceId == null || cityId == null) {
            return Map.of("success", false, "message", "未找到对应的省份或城市，请确认名称");
        }

        AqiFeedbackSubmitDTO dto = new AqiFeedbackSubmitDTO();
        dto.setTelId((String) args.get("telId"));
        dto.setProvinceId(provinceId);
        dto.setCityId(cityId);
        dto.setAddress((String) args.get("address"));
        dto.setEstimatedGrade((Integer) args.get("estimatedGrade"));
        dto.setInformation((String) args.get("information"));

        boolean result = aqiFeedbackService.submitFeedback(dto);
        return Map.of(
                "success", result,
                "message", result ? "反馈提交成功" : "提交失败"
        );
    }

    /**
     * 网格员提交实测数据
     */
    private Object handleSubmitAqiData(Map<String, Object> args) {
        com.neusoft.nep.dto.AqiDataSubmitDTO dto = new com.neusoft.nep.dto.AqiDataSubmitDTO();
        dto.setAfId(((Number) args.get("afId")).intValue());
        dto.setSo2Value(((Number) args.get("so2Value")).intValue());
        dto.setCoValue(((Number) args.get("coValue")).intValue());
        dto.setSpmValue(((Number) args.get("spmValue")).intValue());
        dto.setGmId((String) args.get("gmId"));

        boolean result = statisticsService.submitAqiData(dto);
        return Map.of(
                "success", result,
                "message", result ? "实测数据提交成功，等待管理员确认" : "提交失败"
        );
    }

    /**
     * 管理员指派网格员
     */
    /**
     * 智能指派网格员：优先本地，否则就近异地
     */
    private Object handleSmartAssign(Map<String, Object> args) {
        Integer afId = args.get("afId") != null
                ? ((Number) args.get("afId")).intValue() : null;
        String preferredGmId = (String) args.get("preferredGmId");

        if (afId == null) {
            return Map.of("success", false, "message", "反馈编号不能为空");
        }

        // 1. 查反馈信息
        AqiFeedbackVO feedback = aqiFeedbackService.getFeedbackDetail(afId);
        if (feedback == null) {
            return Map.of("success", false, "message", "反馈记录不存在");
        }
        if (feedback.getState() != 0) {
            return Map.of("success", false, "message", "该反馈已被指派或确认");
        }

        Integer provinceId = feedback.getProvinceId();
        Integer cityId = feedback.getCityId();

        // 2. 如果用户指定了网格员，先校验该网格员是否可工作
        if (preferredGmId != null && !preferredGmId.isEmpty()) {
            GridMember gm = gridMemberService.findByGmId(preferredGmId);
            if (gm == null) {
                return Map.of("success", false, "message", "网格员 " + preferredGmId + " 不存在");
            }
            if (gm.getState() != 0) {
                return Map.of(
                        "success", false,
                        "message", "网格员 " + gm.getGmName() + " 当前状态为「" +
                                getGridMemberStateName(gm.getState()) + "」，无法指派",
                        "suggestion", "建议选择其他可工作的网格员"
                );
            }
            // 直接指派
            boolean ok = aqiFeedbackService.assignGridMember(afId, preferredGmId);
            return buildAssignResult(ok, afId, gm);
        }

        // 3. 未指定网格员：优先本地
        List<GridMember> localMembers = gridMemberService.findAvailableByArea(provinceId, cityId);
        if (!localMembers.isEmpty()) {
            GridMember chosen = localMembers.get(0);
            boolean ok = aqiFeedbackService.assignGridMember(afId, chosen.getGmId());
            Map<String, Object> result = buildAssignResult(ok, afId, chosen);
            result.put("assignType", "本地指派");
            result.put("reason", "反馈任务所在区域有可工作的网格员");
            return result;
        }

        // 4. 本地无：就近异地（同省优先）
        List<GridMember> provinceMembers = gridMemberService.findAvailableByProvince(provinceId);
        if (!provinceMembers.isEmpty()) {
            GridMember chosen = provinceMembers.get(0);
            boolean ok = aqiFeedbackService.assignGridMember(afId, chosen.getGmId());
            Map<String, Object> result = buildAssignResult(ok, afId, chosen);
            result.put("assignType", "异地指派");
            result.put("reason", "本地无可用网格员，已就近指派同省其他市的网格员");
            return result;
        }

        // 5. 全省无：跨省指派（取第一个可工作的）
        List<GridMember> allAvailable = gridMemberService.findAllAvailable();
        if (!allAvailable.isEmpty()) {
            GridMember chosen = allAvailable.get(0);
            boolean ok = aqiFeedbackService.assignGridMember(afId, chosen.getGmId());
            Map<String, Object> result = buildAssignResult(ok, afId, chosen);
            result.put("assignType", "跨省指派");
            result.put("reason", "本省无可用网格员，已跨省指派");
            return result;
        }

        // 6. 无人可派
        return Map.of(
                "success", false,
                "message", "当前没有任何可工作的网格员",
                "suggestion", "请先注册网格员或等待网格员状态恢复为'可工作'"
        );
    }

    private Map<String, Object> buildAssignResult(boolean ok, Integer afId, GridMember gm) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", ok);
        result.put("afId", afId);
        result.put("gmId", gm.getGmId());
        result.put("gmName", gm.getGmName());
        result.put("gmTel", gm.getTel());
        result.put("message", ok
                ? "反馈 " + afId + " 已成功指派给网格员 " + gm.getGmName()
                : "指派失败");
        return result;
    }

    private String getGridMemberStateName(Integer state) {
        if (state == null) return "未知";
        switch (state) {
            case 0: return "可工作";
            case 1: return "临时抽调";
            case 2: return "休假";
            case 3: return "其它";
            default: return "未知";
        }
    }

    // ========== 辅助方法 ==========

    private Integer findProvinceIdByName(String provinceName) {
        if (provinceName == null) return null;
        List<com.neusoft.nep.entity.GridProvince> provinces =
                ((com.neusoft.nep.service.impl.GridAreaServiceImpl) getGridAreaService()).findAllProvinces();
        for (com.neusoft.nep.entity.GridProvince p : provinces) {
            if (p.getProvinceName().equals(provinceName)
                    || p.getProvinceName().contains(provinceName)
                    || provinceName.contains(p.getProvinceAbbr())) {
                return p.getProvinceId();
            }
        }
        return null;
    }

    private Integer findCityIdByName(Integer provinceId, String cityName) {
        if (provinceId == null || cityName == null) return null;
        List<com.neusoft.nep.entity.GridCity> cities =
                ((com.neusoft.nep.service.impl.GridAreaServiceImpl) getGridAreaService())
                        .findCitiesByProvinceId(provinceId);
        for (com.neusoft.nep.entity.GridCity c : cities) {
            if (c.getCityName().equals(cityName) || c.getCityName().contains(cityName)) {
                return c.getCityId();
            }
        }
        return null;
    }

    @Autowired
    private com.neusoft.nep.service.GridAreaService gridAreaService;

    private com.neusoft.nep.service.GridAreaService getGridAreaService() {
        return gridAreaService;
    }

    // ========== 决策者工具实现 ==========

    /**
     * 污染物代码 -> 字段名映射
     */
    private String getPollutantField(String pollutant) {
        if (pollutant == null) return "aqiExceedCount";
        switch (pollutant.toLowerCase()) {
            case "so2": return "so2ExceedCount";
            case "co":  return "coExceedCount";
            case "pm25":
            case "spm": return "spmExceedCount";
            case "aqi":
            default:    return "aqiExceedCount";
        }
    }

    /**
     * 污染物代码 -> 中文名
     */
    private String getPollutantName(String pollutant) {
        if (pollutant == null) return "AQI";
        switch (pollutant.toLowerCase()) {
            case "so2": return "SO₂";
            case "co":  return "CO";
            case "pm25":
            case "spm": return "PM2.5";
            case "aqi":
            default:    return "AQI";
        }
    }

    /**
     * 工具1：查询指定省份指定污染物超标
     */
    private Object handleQueryProvincePollutant(Map<String, Object> args) {
        String provinceName = (String) args.get("provinceName");
        String pollutant = (String) args.get("pollutant");
        String startDate = (String) args.get("startDate");
        String endDate = (String) args.get("endDate");

        System.out.println("查询省份污染物统计: province=" + provinceName
                + ", pollutant=" + pollutant
                + ", startDate=" + startDate
                + ", endDate=" + endDate);

        StatisticsQueryDTO dto = new StatisticsQueryDTO();
        if (startDate != null && !startDate.isEmpty()) dto.setStartDate(startDate);
        if (endDate != null && !endDate.isEmpty()) dto.setEndDate(endDate);

        List<ProvinceStatisticsVO> all = statisticsService.getProvinceStatistics(dto);

        for (ProvinceStatisticsVO vo : all) {
            if (vo.getProvinceName() != null && vo.getProvinceName().contains(provinceName)) {
                Map<String, Object> result = new HashMap<>();
                result.put("province", vo.getProvinceName());
                result.put("pollutant", getPollutantName(pollutant));
                result.put("exceedCount", getFieldValue(vo, pollutant));
                result.put("totalCount", vo.getTotalCount());
                result.put("startDate", startDate != null ? startDate : "不限");
                result.put("endDate", endDate != null ? endDate : "不限");

                // ⭐ 明确告诉 AI 数据的时间范围
                if (startDate == null && endDate == null) {
                    result.put("dataScope", "全部历史数据");
                } else {
                    result.put("dataScope", startDate + " 至 " + endDate);
                }

                return result;
            }
        }

        // ⭐ 该时间段没有数据时，返回明确的空结果
        Map<String, Object> empty = new HashMap<>();
        empty.put("province", provinceName);
        empty.put("pollutant", getPollutantName(pollutant));
        empty.put("exceedCount", 0);
        empty.put("totalCount", 0);
        empty.put("dataScope", (startDate != null ? startDate : "不限") + " 至 " + (endDate != null ? endDate : "不限"));
        empty.put("message", "该时间段内未找到省份数据或无超标记录");
        return empty;
    }

    /**
     * 工具2：超标排名
     */
    private Object handleQueryPollutantRanking(Map<String, Object> args) {
        String pollutant = (String) args.get("pollutant");
        Integer topN = args.get("topN") != null ? ((Number) args.get("topN")).intValue() : 5;

        List<ProvinceStatisticsVO> all = statisticsService.getProvinceStatistics(null);

        // 按污染物字段排序
        String field = getPollutantField(pollutant);
        all.sort((a, b) -> {
            Integer va = getFieldValue(a, pollutant);
            Integer vb = getFieldValue(b, pollutant);
            return Integer.compare(vb != null ? vb : 0, va != null ? va : 0);
        });

        List<Map<String, Object>> topList = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, all.size()); i++) {
            ProvinceStatisticsVO vo = all.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i + 1);
            item.put("province", vo.getProvinceName());
            item.put("exceedCount", getFieldValue(vo, pollutant));
            topList.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("pollutant", getPollutantName(pollutant));
        result.put("ranking", topList);
        return result;
    }

    private Integer getFieldValue(ProvinceStatisticsVO vo, String pollutant) {
        if (pollutant == null) return vo.getAqiExceedCount();
        switch (pollutant.toLowerCase()) {
            case "so2": return vo.getSo2ExceedCount();
            case "co":  return vo.getCoExceedCount();
            case "pm25":
            case "spm": return vo.getSpmExceedCount();
            case "aqi":
            default:    return vo.getAqiExceedCount();
        }
    }

    /**
     * 工具3：AQI 趋势
     */
    private Object handleQueryAqiTrend(Map<String, Object> args) {
        List<TrendStatisticsVO> trend = statisticsService.getAqiTrend();
        Map<String, Object> result = new HashMap<>();
        result.put("province", args.get("provinceName"));
        result.put("trend", trend);
        return result;
    }

    /**
     * 工具4：对比两个省份
     */
    private Object handleCompareProvinces(Map<String, Object> args) {
        String a = (String) args.get("provinceA");
        String b = (String) args.get("provinceB");
        String pollutant = (String) args.get("pollutant");

        List<ProvinceStatisticsVO> all = statisticsService.getProvinceStatistics(null);

        ProvinceStatisticsVO voA = null, voB = null;
        for (ProvinceStatisticsVO vo : all) {
            if (vo.getProvinceName() != null) {
                if (vo.getProvinceName().contains(a)) voA = vo;
                if (vo.getProvinceName().contains(b)) voB = vo;
            }
        }

        if (voA == null || voB == null) {
            return Map.of("error", "未找到指定省份");
        }

        Integer valueA = getFieldValue(voA, pollutant);
        Integer valueB = getFieldValue(voB, pollutant);

        Map<String, Object> result = new HashMap<>();
        result.put("pollutant", getPollutantName(pollutant));
        result.put("provinceA", voA.getProvinceName());
        result.put("valueA", valueA);
        result.put("provinceB", voB.getProvinceName());
        result.put("valueB", valueB);
        result.put("diff", valueA - valueB);

        if (valueA > valueB) {
            result.put("winner", voA.getProvinceName() + " 比 " + voB.getProvinceName() + " 多 " + (valueA - valueB) + " 次超标");
        } else if (valueA < valueB) {
            result.put("winner", voB.getProvinceName() + " 比 " + voA.getProvinceName() + " 多 " + (valueB - valueA) + " 次超标");
        } else {
            result.put("winner", "两省持平");
        }

        return result;
    }

    /**
     * 工具5：对比两个时间段
     */
    private Object handleCompareTimePeriods(Map<String, Object> args) {
        String p1s = (String) args.get("period1Start");
        String p1e = (String) args.get("period1End");
        String p2s = (String) args.get("period2Start");
        String p2e = (String) args.get("period2End");
        String province = (String) args.get("provinceName");

        StatisticsQueryDTO dto1 = new StatisticsQueryDTO();
        dto1.setStartDate(p1s);
        dto1.setEndDate(p1e);
        List<ProvinceStatisticsVO> list1 = statisticsService.getProvinceStatistics(dto1);

        StatisticsQueryDTO dto2 = new StatisticsQueryDTO();
        dto2.setStartDate(p2s);
        dto2.setEndDate(p2e);
        List<ProvinceStatisticsVO> list2 = statisticsService.getProvinceStatistics(dto2);

        int total1 = sumAqi(list1, province);
        int total2 = sumAqi(list2, province);

        Map<String, Object> result = new HashMap<>();
        result.put("period1", p1s + " ~ " + p1e);
        result.put("period1Total", total1);
        result.put("period2", p2s + " ~ " + p2e);
        result.put("period2Total", total2);
        result.put("diff", total2 - total1);
        return result;
    }

    private int sumAqi(List<ProvinceStatisticsVO> list, String province) {
        int sum = 0;
        for (ProvinceStatisticsVO vo : list) {
            if (province == null || (vo.getProvinceName() != null && vo.getProvinceName().contains(province))) {
                sum += vo.getAqiExceedCount() != null ? vo.getAqiExceedCount() : 0;
            }
        }
        return sum;
    }

    /**
     * 工具6：异常检测
     */
    private Object handleDetectAnomaly(Map<String, Object> args) {
        String pollutant = (String) args.get("pollutant");

        // 检测月度异常：某月 AQI 超标量 > 平均值 + 2 倍标准差
        List<TrendStatisticsVO> trend = statisticsService.getAqiTrend();
        if (trend == null || trend.isEmpty()) {
            return Map.of("anomalies", new ArrayList<>());
        }

        // 计算均值和标准差
        double sum = 0;
        for (TrendStatisticsVO vo : trend) {
            sum += vo.getExceedCount() != null ? vo.getExceedCount() : 0;
        }
        double mean = sum / trend.size();

        double variance = 0;
        for (TrendStatisticsVO vo : trend) {
            double diff = (vo.getExceedCount() != null ? vo.getExceedCount() : 0) - mean;
            variance += diff * diff;
        }
        double stdDev = Math.sqrt(variance / trend.size());

        // 找出异常点
        List<Map<String, Object>> anomalies = new ArrayList<>();
        double threshold = mean + 2 * stdDev;

        for (TrendStatisticsVO vo : trend) {
            int count = vo.getExceedCount() != null ? vo.getExceedCount() : 0;
            if (count > threshold) {
                Map<String, Object> anomaly = new HashMap<>();
                anomaly.put("month", vo.getMonth());
                anomaly.put("exceedCount", count);
                anomaly.put("average", Math.round(mean * 100) / 100.0);
                anomaly.put("threshold", Math.round(threshold * 100) / 100.0);
                anomaly.put("deviation", Math.round((count - mean) * 100) / 100.0);
                anomalies.add(anomaly);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("pollutant", getPollutantName(pollutant));
        result.put("average", Math.round(mean * 100) / 100.0);
        result.put("stdDev", Math.round(stdDev * 100) / 100.0);
        result.put("anomalies", anomalies);

        if (!anomalies.isEmpty()) {
            result.put("suggestion", "检测到 " + anomalies.size() + " 个异常数据点，建议核查原始数据或该月监测情况。");
        } else {
            result.put("suggestion", "未检测到明显异常，数据分布正常。");
        }

        return result;
    }

    /**
     * 工具9：查询某省城市分布
     */
    private Object handleQueryCitiesByProvince(Map<String, Object> args) {
        String provinceName = (String) args.get("provinceName");
        Integer provinceId = findProvinceIdByName(provinceName);

        if (provinceId == null) {
            return Map.of("error", "未找到省份：" + provinceName);
        }

        List<CityStatisticsVO> cities = statisticsService.getCityStatisticsByProvince(provinceId);
        Map<String, Object> result = new HashMap<>();
        result.put("province", provinceName);
        result.put("cities", cities);
        return result;
    }
}