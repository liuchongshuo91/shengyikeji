package com.kjd.backend.config;

import com.kjd.backend.entity.Reimbursement;
import com.kjd.backend.mapper.ReimbursementMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;
    private final ReimbursementMapper reimbursementMapper;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate, ReimbursementMapper reimbursementMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.reimbursementMapper = reimbursementMapper;
    }

    @PostConstruct
    public void init() {
        createTables();
        migrateTables();
        Integer count = jdbcTemplate.queryForObject("select count(1) from fk_reim_main", Integer.class);
        if (count != null && count == 0) {
            seed();
        }
    }

    private void createTables() {
        jdbcTemplate.execute("""
                create table if not exists fk_reim_main (
                  id bigint auto_increment primary key,
                  creation_time datetime(6),
                  reimbursement_title varchar(500),
                  reimburser_id varchar(32),
                  reimburser_no varchar(20),
                  reimburser_name varchar(20),
                  reim_department_id varchar(32),
                  reim_department_no varchar(20),
                  reim_department_name varchar(40),
                  reim_company_id varchar(32),
                  reim_company_no varchar(20),
                  reim_company_name varchar(40),
                  business_type_id varchar(32),
                  business_type_no varchar(20),
                  business_type_name varchar(40),
                  business_trip_reason varchar(500),
                  document_type varchar(50) default '日常报销单',
                  subsidy_total varchar(20),
                  meal_allowance varchar(20),
                  transportation_allowance varchar(20),
                  phone_allowance varchar(20),
                  remarks varchar(1000),
                  status int,
                  reim_no varchar(32),
                  submit_date date,
                  allocations_json text,
                  version int default 0
                )
                """);
        jdbcTemplate.execute("""
                create table if not exists fk_reim_itinerary (
                  id bigint auto_increment primary key,
                  main_id bigint not null,
                  traveler_id varchar(20),
                  traveler_no varchar(32),
                  traveler_name varchar(20),
                  departure_date varchar(20),
                  arrival_date varchar(20),
                  departure_city varchar(20),
                  departure_city_no varchar(20),
                  arriving_city varchar(20),
                  arriving_city_no varchar(20),
                  itinerary_instructions varchar(500)
                )
                """);
        jdbcTemplate.execute("""
                create table if not exists fk_reim_subsidy (
                  id bigint auto_increment primary key,
                  main_id bigint not null,
                  traveler_id varchar(20),
                  traveler_no varchar(20),
                  traveler_name varchar(20),
                  departure_date varchar(20),
                  arrival_date varchar(20),
                  subsidy_days varchar(20),
                  departure_city varchar(20),
                  departure_city_no varchar(20),
                  arriving_city varchar(20),
                  arriving_city_no varchar(20),
                  application_amount varchar(20),
                  subsidy_amount varchar(20),
                  meal_allowance varchar(20),
                  transportation_allowance varchar(20),
                  phone_allowance varchar(20),
                  business_type_id varchar(32),
                  business_type_no varchar(20),
                  business_type_name varchar(40),
                  trip_id bigint
                )
                """);
        jdbcTemplate.execute("""
                create table if not exists fk_subsidy_calendar (
                  id bigint auto_increment primary key,
                  main_id bigint not null,
                  travel_date varchar(20) not null,
                  travel_date_week varchar(32),
                  subsidized_cities varchar(32),
                  subsidized_city_number varchar(32),
                  remark varchar(100),
                  standard_meal_expenses_amount varchar(32),
                  standard_traffic_amount varchar(32),
                  standard_communication_amount varchar(32),
                  meal_expenses_amount varchar(32),
                  traffic_amount varchar(32),
                  communication_amount varchar(32),
                  is_reimbursed varchar(32)
                )
                """);
    }

    private void migrateTables() {
        migrateCreationTimeColumn();
        addMainColumns();
        addDocumentTypeColumn();
        addItineraryColumns();
        addSubsidyColumns();
        addCalendarColumns();
        modifyColumn("fk_reim_main", "reim_no", "varchar(32) null");
        relaxLegacyRequiredColumns("fk_reim_main");
        relaxLegacyRequiredColumns("fk_reim_itinerary");
        relaxLegacyRequiredColumns("fk_reim_subsidy");
        relaxLegacyRequiredColumns("fk_subsidy_calendar");
        if (columnExists("fk_reim_main", "reimbursement_no")) {
            jdbcTemplate.execute("update fk_reim_main set reim_no = reimbursement_no where (reim_no = '' or reim_no is null) and reimbursement_no is not null and reimbursement_no <> ''");
        }
        jdbcTemplate.execute("update fk_reim_main set reim_no = id where reim_no = '' or reim_no is null");
    }

    private void migrateCreationTimeColumn() {
        String colType = null;
        try {
            colType = jdbcTemplate.queryForObject("""
                    select data_type from information_schema.columns
                    where table_schema = database()
                      and table_name = 'fk_reim_main'
                      and column_name = 'creation_time'
                    """, String.class);
        } catch (Exception ignored) {}
        if ("varchar".equalsIgnoreCase(colType)) {
            jdbcTemplate.execute("update fk_reim_main set creation_time = replace(creation_time, 'T', ' ') where creation_time like '%T%'");
            jdbcTemplate.execute("alter table fk_reim_main modify column creation_time datetime(6)");
        }
    }

    private void addMainColumns() {
        addColumn("fk_reim_main", "creation_time", "datetime(6)");
        addColumn("fk_reim_main", "reimbursement_title", "varchar(500)");
        addColumn("fk_reim_main", "reimburser_id", "varchar(32)");
        addColumn("fk_reim_main", "reimburser_no", "varchar(20)");
        addColumn("fk_reim_main", "reimburser_name", "varchar(20)");
        addColumn("fk_reim_main", "reim_department_id", "varchar(32)");
        addColumn("fk_reim_main", "reim_department_no", "varchar(20)");
        addColumn("fk_reim_main", "reim_department_name", "varchar(40)");
        addColumn("fk_reim_main", "reim_company_id", "varchar(32)");
        addColumn("fk_reim_main", "reim_company_no", "varchar(20)");
        addColumn("fk_reim_main", "reim_company_name", "varchar(40)");
        addColumn("fk_reim_main", "business_type_id", "varchar(32)");
        addColumn("fk_reim_main", "business_type_no", "varchar(20)");
        addColumn("fk_reim_main", "business_type_name", "varchar(40)");
        addColumn("fk_reim_main", "business_trip_reason", "varchar(500)");
        addColumn("fk_reim_main", "subsidy_total", "varchar(20)");
        addColumn("fk_reim_main", "meal_allowance", "varchar(20)");
        addColumn("fk_reim_main", "transportation_allowance", "varchar(20)");
        addColumn("fk_reim_main", "phone_allowance", "varchar(20)");
        addColumn("fk_reim_main", "remarks", "varchar(1000)");
        addColumn("fk_reim_main", "status", "int");
        addColumn("fk_reim_main", "reim_no", "varchar(32)");
        addColumn("fk_reim_main", "submit_date", "date");
        addColumn("fk_reim_main", "allocations_json", "text");
        addColumn("fk_reim_main", "version", "int default 0");
    }

    private void addDocumentTypeColumn() {
        addColumn("fk_reim_main", "document_type", "varchar(50) default '日常报销单'");
    }

    private void addItineraryColumns() {
        addColumn("fk_reim_itinerary", "main_id", "bigint");
        addColumn("fk_reim_itinerary", "traveler_id", "varchar(20)");
        addColumn("fk_reim_itinerary", "traveler_no", "varchar(32)");
        addColumn("fk_reim_itinerary", "traveler_name", "varchar(20)");
        addColumn("fk_reim_itinerary", "departure_date", "varchar(20)");
        addColumn("fk_reim_itinerary", "arrival_date", "varchar(20)");
        addColumn("fk_reim_itinerary", "departure_city", "varchar(20)");
        addColumn("fk_reim_itinerary", "departure_city_no", "varchar(20)");
        addColumn("fk_reim_itinerary", "arriving_city", "varchar(20)");
        addColumn("fk_reim_itinerary", "arriving_city_no", "varchar(20)");
        addColumn("fk_reim_itinerary", "itinerary_instructions", "varchar(500)");
    }

    private void addSubsidyColumns() {
        addColumn("fk_reim_subsidy", "main_id", "bigint");
        addColumn("fk_reim_subsidy", "traveler_id", "varchar(20)");
        addColumn("fk_reim_subsidy", "traveler_no", "varchar(20)");
        addColumn("fk_reim_subsidy", "traveler_name", "varchar(20)");
        addColumn("fk_reim_subsidy", "departure_date", "varchar(20)");
        addColumn("fk_reim_subsidy", "arrival_date", "varchar(20)");
        addColumn("fk_reim_subsidy", "subsidy_days", "varchar(20)");
        addColumn("fk_reim_subsidy", "departure_city", "varchar(20)");
        addColumn("fk_reim_subsidy", "departure_city_no", "varchar(20)");
        addColumn("fk_reim_subsidy", "arriving_city", "varchar(20)");
        addColumn("fk_reim_subsidy", "arriving_city_no", "varchar(20)");
        addColumn("fk_reim_subsidy", "application_amount", "varchar(20)");
        addColumn("fk_reim_subsidy", "subsidy_amount", "varchar(20)");
        addColumn("fk_reim_subsidy", "meal_allowance", "varchar(20)");
        addColumn("fk_reim_subsidy", "transportation_allowance", "varchar(20)");
        addColumn("fk_reim_subsidy", "phone_allowance", "varchar(20)");
        addColumn("fk_reim_subsidy", "business_type_id", "varchar(32)");
        addColumn("fk_reim_subsidy", "business_type_no", "varchar(20)");
        addColumn("fk_reim_subsidy", "business_type_name", "varchar(40)");
        addColumn("fk_reim_subsidy", "trip_id", "bigint");
    }

    private void addCalendarColumns() {
        addColumn("fk_subsidy_calendar", "main_id", "bigint");
        addColumn("fk_subsidy_calendar", "travel_date", "varchar(20)");
        addColumn("fk_subsidy_calendar", "travel_date_week", "varchar(32)");
        addColumn("fk_subsidy_calendar", "subsidized_cities", "varchar(32)");
        addColumn("fk_subsidy_calendar", "subsidized_city_number", "varchar(32)");
        addColumn("fk_subsidy_calendar", "remark", "varchar(100)");
        addColumn("fk_subsidy_calendar", "standard_meal_expenses_amount", "varchar(32)");
        addColumn("fk_subsidy_calendar", "standard_traffic_amount", "varchar(32)");
        addColumn("fk_subsidy_calendar", "standard_communication_amount", "varchar(32)");
        addColumn("fk_subsidy_calendar", "meal_expenses_amount", "varchar(32)");
        addColumn("fk_subsidy_calendar", "traffic_amount", "varchar(32)");
        addColumn("fk_subsidy_calendar", "communication_amount", "varchar(32)");
        addColumn("fk_subsidy_calendar", "is_reimbursed", "varchar(32)");
    }

    private void seed() {
        for (int i = 0; i < 10; i++) {
            Reimbursement item = new Reimbursement();
            item.setReimbursementNo("RCBX202605" + String.format("%04d", 15002 - i * 37));
            item.setCreationTime(LocalDateTime.now().minusDays(i + 3L));
            item.setSubmitDate(LocalDate.now().minusDays(i + 2L));
            item.setStatus(i == 8 || i == 9 ? 2 : (i == 4 || i == 5 || i == 6 || i == 7 ? 0 : 1));
            item.setReimbursementTitle(i < 4 ? "日常报销单模板 - 副本，" + (40 + i * 83) + ".00CNY，..." : (i < 7 ? "测试" : ""));
            item.setBusinessTripReason(i == 7 || i == 9 ? "这个法人公司的名字可能会有点长是我..." : "");
            item.setReimburserId("13AB3A3F72409002");
            item.setReimburserNo("202101497");
            item.setReimburserName("徐年年");
            item.setReimDepartmentId("13C7E2BAE0393001");
            item.setReimDepartmentNo("072006");
            item.setReimDepartmentName("运营事业部");
            item.setReimCompanyId("1C54557F1782E000");
            item.setReimCompanyName("胜意科技北京分公司");
            item.setBusinessTypeId("1B5FEB7DD4396000");
            item.setBusinessTypeName("项目出差");
            item.setDocumentType("日常报销单");
            item.setSubsidyTotal(BigDecimal.ZERO);
            reimbursementMapper.insert(item);
        }
    }

    private void addColumn(String table, String column, String definition) {
        if (!columnExists(table, column)) {
            jdbcTemplate.execute("alter table " + table + " add column `" + column + "` " + definition);
        }
    }

    private boolean columnExists(String table, String column) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(1)
                from information_schema.columns
                where table_schema = database()
                  and table_name = ?
                  and column_name = ?
                """, Integer.class, table, column);
        return count != null && count > 0;
    }

    private void modifyColumn(String table, String column, String definition) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(1)
                from information_schema.columns
                where table_schema = database()
                  and table_name = ?
                  and column_name = ?
                """, Integer.class, table, column);
        if (count != null && count > 0) {
            jdbcTemplate.execute("alter table " + table + " modify column `" + column + "` " + definition);
        }
    }

    private void relaxLegacyRequiredColumns(String table) {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList("""
                select column_name, column_type
                from information_schema.columns
                where table_schema = database()
                  and table_name = ?
                  and is_nullable = 'NO'
                  and column_default is null
                  and extra not like '%auto_increment%'
                  and column_name <> 'id'
                """, table);
        for (Map<String, Object> column : columns) {
            String name = String.valueOf(column.get("column_name"));
            String type = String.valueOf(column.get("column_type"));
            jdbcTemplate.execute("alter table " + table + " modify column `" + name + "` " + type + " null");
        }
    }
}
