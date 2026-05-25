package com.kjd.backend.service.impl;

import com.kjd.backend.dto.ReimbursementQueryDTO;
import com.kjd.backend.entity.Allocation;
import com.kjd.backend.entity.CalendarDay;
import com.kjd.backend.entity.Reimbursement;
import com.kjd.backend.entity.Subsidy;
import com.kjd.backend.entity.Trip;
import com.kjd.backend.mapper.CalendarDayMapper;
import com.kjd.backend.mapper.ReimbursementMapper;
import com.kjd.backend.mapper.SubsidyMapper;
import com.kjd.backend.mapper.TripMapper;
import com.kjd.backend.service.DictionaryService;
import com.kjd.backend.service.ReimbursementService;
import com.kjd.backend.vo.DictionaryItemVO;
import com.kjd.backend.vo.PageResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ReimbursementServiceImpl implements ReimbursementService {
    private final ReimbursementMapper reimbursementMapper;
    private final TripMapper tripMapper;
    private final SubsidyMapper subsidyMapper;
    private final CalendarDayMapper calendarDayMapper;
    private final DictionaryService dictionaries;

    public ReimbursementServiceImpl(ReimbursementMapper reimbursementMapper,
                                    TripMapper tripMapper,
                                    SubsidyMapper subsidyMapper,
                                    CalendarDayMapper calendarDayMapper,
                                    DictionaryService dictionaries) {
        this.reimbursementMapper = reimbursementMapper;
        this.tripMapper = tripMapper;
        this.subsidyMapper = subsidyMapper;
        this.calendarDayMapper = calendarDayMapper;
        this.dictionaries = dictionaries;
    }

    @Override
    public PageResultVO<Reimbursement> page(ReimbursementQueryDTO query) {
        int page = Math.max(query.page, 1);
        int size = Math.max(query.size, 1);

        Long total = reimbursementMapper.selectCountWithCondition(
                query.reimbursementNo,
                query.reimbursementTitle,
                query.businessTripReason,
                query.reimCompanyId,
                query.reimDepartmentId,
                query.reimburserId,
                query.businessTypeId
        );

        List<Reimbursement> records = new ArrayList<>();
        if (total > 0) {
            records = reimbursementMapper.selectPageList(
                    query.reimbursementNo,
                    query.reimbursementTitle,
                    query.businessTripReason,
                    query.reimCompanyId,
                    query.reimDepartmentId,
                    query.reimburserId,
                    query.businessTypeId
            );
            // 手动分页
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, records.size());
            if (fromIndex < records.size()) {
                records = records.subList(fromIndex, toIndex);
            } else {
                records = new ArrayList<>();
            }
            // 设置状态名称
            for (Reimbursement record : records) {
                record.setStatusName(statusName(record.getStatus()));
            }
        }

        return new PageResultVO<>(total == null ? 0 : total, page, size, records);
    }

    @Override
    public Reimbursement find(String id) {
        Reimbursement item = reimbursementMapper.selectById(id);
        if (item != null) {
            item.setStatusName(statusName(item.getStatus()));
            // 加载行程
            List<Trip> trips = tripMapper.selectByMainId(id);
            item.setTrips(trips);
            // 加载补助
            List<Subsidy> subsidies = subsidyMapper.selectByMainId(id);
            for (Subsidy subsidy : subsidies) {
                subsidy.setTripDateRange(formatTripDateRange(item, subsidy));
                subsidy.setRoute(formatRoute(item, subsidy));
                // 加载日历明细
                List<CalendarDay> calendar = calendarDayMapper.selectBySubsidyId(subsidy.getId());
                for (CalendarDay day : calendar) {
                    parseReimbursed(day);
                }
                subsidy.setCalendar(calendar);
            }
            item.setSubsidies(subsidies);
        }
        return item;
    }

    @Override
    @Transactional
    public Reimbursement save(Reimbursement item, boolean submit) {
        validateHeader(item, submit);
        rebuildSubsidies(item);
        rebuildTotals(item);
        validateTrips(item, submit);
        validateSubsidies(item);
        validateAllocations(item, submit);
        item.setStatus(submit ? 1 : 0);

        boolean isNew = !StringUtils.hasText(item.getId());
        if (isNew) {
            item.setId(uuid());
        }
        if (!StringUtils.hasText(item.getReimbursementNo())) {
            item.setReimbursementNo(nextNo());
        }
        if (item.getCreationTime() == null) {
            item.setCreationTime(LocalDateTime.now());
        }
        if (item.getSubmitDate() == null) {
            item.setSubmitDate(LocalDate.now());
        }
        item.setStatusName(statusName(item.getStatus()));

        if (isNew) {
            reimbursementMapper.insert(item);
        } else {
            reimbursementMapper.updateById(item);
        }

        rebuildDetails(item);
        return find(item.getId());
    }

    @Override
    public void voidDocument(String id) {
        reimbursementMapper.updateStatusById(id, 2);
    }

    @Transactional
    protected void rebuildDetails(Reimbursement item) {
        // 删除旧的子表数据
        calendarDayMapper.deleteByMainId(item.getId());
        subsidyMapper.deleteByMainId(item.getId());
        tripMapper.deleteByMainId(item.getId());

        // 插入行程
        for (Trip trip : item.getTrips()) {
            if (!StringUtils.hasText(trip.getId())) {
                trip.setId(uuid());
            }
            trip.setMainId(item.getId());
            tripMapper.insert(trip);
        }

        // 插入补助和日历明细
        for (Subsidy subsidy : item.getSubsidies()) {
            if (!StringUtils.hasText(subsidy.getId())) {
                subsidy.setId(uuid());
            }
            subsidy.setMainId(item.getId());
            subsidyMapper.insert(subsidy);

            for (CalendarDay day : subsidy.getCalendar()) {
                if (!StringUtils.hasText(day.getId())) {
                    day.setId(uuid());
                }
                day.setMainId(subsidy.getId());
                day.setReimbursed(toReimbursedString(day));
                calendarDayMapper.insert(day);
            }
        }
    }

    private void validateHeader(Reimbursement item, boolean submit) {
        if (!submit) return;
        require(item.getReimbursementTitle(), "报销标题不能为空");
        require(item.getReimburserId(), "报销人不能为空");
        require(item.getReimDepartmentId(), "报销部门不能为空");
        require(item.getReimCompanyId(), "费用归属公司不能为空");
        require(item.getBusinessTypeId(), "业务类型不能为空");
        require(item.getBusinessTripReason(), "出差事由不能为空");
    }

    private void validateTrips(Reimbursement item, boolean submit) {
        if (submit && item.getTrips().isEmpty()) throw new IllegalArgumentException("至少录入一条行程");
        Map<String, Set<LocalDate>> occupied = new HashMap<>();
        for (Trip trip : item.getTrips()) {
            require(trip.getTravelerId(), "出行人不能为空");
            require(trip.getFromCityNo(), "出发城市不能为空");
            require(trip.getToCityNo(), "到达城市不能为空");
            if (trip.getStartDate() == null || trip.getEndDate() == null) throw new IllegalArgumentException("出发到达日期不能为空");
            if (trip.getEndDate().isBefore(trip.getStartDate())) throw new IllegalArgumentException("到达日期不可早于出发日期");
            if (trip.getEndDate().isAfter(LocalDate.now())) throw new IllegalArgumentException("到达日期不可晚于当前日期");
            require(trip.getTripDescription(), "行程说明不能为空");
            Set<LocalDate> dates = occupied.computeIfAbsent(trip.getTravelerId(), key -> new HashSet<>());
            for (LocalDate date = trip.getStartDate(); !date.isAfter(trip.getEndDate()); date = date.plusDays(1)) {
                if (!dates.add(date)) throw new IllegalArgumentException("补录行程中同一人员日期范围不可重复");
            }
        }
    }

    private void rebuildSubsidies(Reimbursement item) {
        Map<String, Subsidy> existing = new HashMap<>();
        for (Subsidy subsidy : item.getSubsidies()) existing.put(subsidy.getTripId(), subsidy);
        item.getSubsidies().clear();
        for (Trip trip : item.getTrips()) {
            if (!StringUtils.hasText(trip.getId())) trip.setId(uuid());
            Subsidy subsidy = existing.getOrDefault(trip.getId(), new Subsidy());
            subsidy.setId(StringUtils.hasText(subsidy.getId()) ? subsidy.getId() : uuid());
            subsidy.setTripId(trip.getId());
            subsidy.setTravelerId(trip.getTravelerId());
            subsidy.setTravelerName(trip.getTravelerName());
            subsidy.setTripDateRange(trip.getStartDate() + " 至 " + trip.getEndDate());
            subsidy.setDays(trip.getStartDate() == null || trip.getEndDate() == null ? 0 : (int) ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1);
            subsidy.setRoute(trip.getFromCityName() + "-" + trip.getToCityName());
            subsidy.setSubsidyCity(trip.getToCityName());
            subsidy.setCalendar(rebuildCalendar(trip, subsidy));
            sumSubsidy(subsidy);
            item.getSubsidies().add(subsidy);
        }
    }

    private List<CalendarDay> rebuildCalendar(Trip trip, Subsidy subsidy) {
        Map<LocalDate, CalendarDay> oldDays = new HashMap<>();
        for (CalendarDay day : subsidy.getCalendar()) oldDays.put(day.getTripDate(), day);
        List<CalendarDay> days = new ArrayList<>();
        if (trip.getStartDate() == null || trip.getEndDate() == null) return days;
        BigDecimal mealStandard = mealStandard(trip.getToCityNo());
        for (LocalDate date = trip.getStartDate(); !date.isAfter(trip.getEndDate()); date = date.plusDays(1)) {
            CalendarDay day = oldDays.getOrDefault(date, new CalendarDay());
            day.setTripDate(date);
            day.setWeekName(weekName(date.getDayOfWeek()));
            day.setSubsidyCity(trip.getToCityName());
            day.setMealStandard(mealStandard);
            if (!day.isMealSelected()) day.setMealAmount(BigDecimal.ZERO);
            if (!day.isTransportSelected()) day.setTransportAmount(BigDecimal.ZERO);
            if (!day.isPhoneSelected()) day.setPhoneAmount(BigDecimal.ZERO);
            if (day.isMealSelected() && day.getMealAmount().compareTo(BigDecimal.ZERO) == 0) day.setMealAmount(mealStandard);
            if (day.isTransportSelected() && day.getTransportAmount().compareTo(BigDecimal.ZERO) == 0) day.setTransportAmount(day.getTransportStandard());
            if (day.isPhoneSelected() && day.getPhoneAmount().compareTo(BigDecimal.ZERO) == 0) day.setPhoneAmount(day.getPhoneStandard());
            days.add(day);
        }
        return days;
    }

    private void validateSubsidies(Reimbursement item) {
        for (Subsidy subsidy : item.getSubsidies()) {
            for (CalendarDay day : subsidy.getCalendar()) {
                checkAmount(day.isMealSelected(), day.getMealAmount(), day.getMealStandard(), "餐费补助");
                checkAmount(day.isTransportSelected(), day.getTransportAmount(), day.getTransportStandard(), "交通补助");
                checkAmount(day.isPhoneSelected(), day.getPhoneAmount(), day.getPhoneStandard(), "通讯补助");
            }
            sumSubsidy(subsidy);
        }
    }

    private void validateAllocations(Reimbursement item, boolean submit) {
        if (item.getAllocations().isEmpty()) {
            Allocation first = new Allocation();
            first.setId(uuid());
            first.setReimCompanyId(item.getReimCompanyId());
            first.setReimCompanyNo(item.getReimCompanyNo());
            first.setReimCompanyName(item.getReimCompanyName());
            first.setAllocationRatio(BigDecimal.ONE);
            first.setAllocationAmount(item.getSubsidyTotal());
            item.getAllocations().add(first);
        }
        BigDecimal ratio = BigDecimal.ZERO;
        BigDecimal amount = BigDecimal.ZERO;
        for (Allocation allocation : item.getAllocations()) {
            if (!StringUtils.hasText(allocation.getId())) allocation.setId(uuid());
            if (submit) require(allocation.getReimCompanyId(), "费用归属不能为空");
            ratio = ratio.add(money(allocation.getAllocationRatio()));
            amount = amount.add(money(allocation.getAllocationAmount()));
        }
        if (submit && ratio.compareTo(BigDecimal.ONE) != 0) throw new IllegalArgumentException("分摊比例合计必须为100%");
        if (submit && amount.compareTo(item.getSubsidyTotal()) != 0) throw new IllegalArgumentException("分摊金额合计必须等于费用合计中的补助总金额");
    }

    private void rebuildTotals(Reimbursement item) {
        item.setMealAllowance(BigDecimal.ZERO);
        item.setTransportationAllowance(BigDecimal.ZERO);
        item.setPhoneAllowance(BigDecimal.ZERO);
        for (Subsidy subsidy : item.getSubsidies()) {
            for (CalendarDay day : subsidy.getCalendar()) {
                item.setMealAllowance(item.getMealAllowance().add(day.getMealAmount()));
                item.setTransportationAllowance(item.getTransportationAllowance().add(day.getTransportAmount()));
                item.setPhoneAllowance(item.getPhoneAllowance().add(day.getPhoneAmount()));
            }
        }
        item.setSubsidyTotal(item.getMealAllowance().add(item.getTransportationAllowance()).add(item.getPhoneAllowance()).setScale(2, RoundingMode.HALF_UP));
    }

    private void sumSubsidy(Subsidy subsidy) {
        subsidy.setApplyAmount(BigDecimal.ZERO);
        subsidy.setSubsidyAmount(BigDecimal.ZERO);
        for (CalendarDay day : subsidy.getCalendar()) {
            subsidy.setApplyAmount(subsidy.getApplyAmount()
                    .add(day.isMealSelected() ? day.getMealStandard() : BigDecimal.ZERO)
                    .add(day.isTransportSelected() ? day.getTransportStandard() : BigDecimal.ZERO)
                    .add(day.isPhoneSelected() ? day.getPhoneStandard() : BigDecimal.ZERO));
            subsidy.setSubsidyAmount(subsidy.getSubsidyAmount().add(day.getMealAmount()).add(day.getTransportAmount()).add(day.getPhoneAmount()));
        }
        subsidy.setApplyAmount(subsidy.getApplyAmount().setScale(2, RoundingMode.HALF_UP));
        subsidy.setSubsidyAmount(subsidy.getSubsidyAmount().setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal mealStandard(String cityNo) {
        String cityType = dictionaries.city(cityNo).map(DictionaryItemVO::type).orElse("3");
        if ("1".equals(cityType)) return new BigDecimal("100.00");
        if ("2".equals(cityType)) return new BigDecimal("80.00");
        return new BigDecimal("50.00");
    }

    private String formatTripDateRange(Reimbursement item, Subsidy subsidy) {
        Trip trip = item.getTrips().stream().filter(t -> t.getId().equals(subsidy.getTripId())).findFirst().orElse(null);
        if (trip == null) return "";
        return trip.getStartDate() + " 至 " + trip.getEndDate();
    }

    private String formatRoute(Reimbursement item, Subsidy subsidy) {
        Trip trip = item.getTrips().stream().filter(t -> t.getId().equals(subsidy.getTripId())).findFirst().orElse(null);
        if (trip == null) return "";
        return trip.getFromCityName() + "-" + trip.getToCityName();
    }

    private static void checkAmount(boolean selected, BigDecimal amount, BigDecimal standard, String label) {
        if (!selected && money(amount).compareTo(BigDecimal.ZERO) != 0) throw new IllegalArgumentException(label + "未选中时金额必须为0");
        if (selected && (money(amount).compareTo(BigDecimal.ZERO) < 0 || money(amount).compareTo(standard) > 0)) {
            throw new IllegalArgumentException(label + "金额需为正数且不可大于标准金额");
        }
    }

    private static void require(String value, String message) {
        if (!StringUtils.hasText(value)) throw new IllegalArgumentException(message);
    }

    private static BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
    }

    private static String weekName(DayOfWeek week) {
        return switch (week) {
            case MONDAY -> "星期一";
            case TUESDAY -> "星期二";
            case WEDNESDAY -> "星期三";
            case THURSDAY -> "星期四";
            case FRIDAY -> "星期五";
            case SATURDAY -> "星期六";
            case SUNDAY -> "星期日";
        };
    }

    private static String statusName(Integer status) {
        if (status == null || status == 0) return "草稿";
        if (status == 1) return "已完成";
        if (status == 2) return "已作废";
        return "未知";
    }

    private static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String nextNo() {
        return "CLBX" + LocalDate.now().toString().replace("-", "") + System.currentTimeMillis() % 100000;
    }

    private static String toReimbursedString(CalendarDay day) {
        List<String> selected = new ArrayList<>();
        if (day.isMealSelected()) selected.add("meal");
        if (day.isTransportSelected()) selected.add("transport");
        if (day.isPhoneSelected()) selected.add("phone");
        return String.join(",", selected);
    }

    private static void parseReimbursed(CalendarDay day) {
        String reimbursed = day.getReimbursed();
        day.setMealSelected(reimbursed != null && reimbursed.contains("meal"));
        day.setTransportSelected(reimbursed != null && reimbursed.contains("transport"));
        day.setPhoneSelected(reimbursed != null && reimbursed.contains("phone"));
    }
}
