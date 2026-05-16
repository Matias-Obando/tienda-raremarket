package com.raremarket.backend.service;

import com.raremarket.backend.dto.admin.AdminMetricsResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminMetricsService {
    private static final int DEFAULT_WINDOW_DAYS = 30;
    private static final int MIN_WINDOW_DAYS = 7;
    private static final int MAX_WINDOW_DAYS = 90;
    private static final ZoneId ZONE = ZoneId.systemDefault();

    private final JdbcTemplate jdbcTemplate;

    public AdminMetricsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AdminMetricsResponse getMetrics(int requestedDays) {
        int windowDays = normalizeWindowDays(requestedDays);
        LocalDate endDate = LocalDate.now(ZONE);
        LocalDate startDate = endDate.minusDays(windowDays - 1L);
        OffsetDateTime since = startDate.atStartOfDay(ZONE).toOffsetDateTime();

        AdminMetricsResponse response = new AdminMetricsResponse();
        response.setSummary(loadSummary(since));
        response.setOrdersTrend(buildSeries(
                loadDailyValueMap(
                        """
                                SELECT to_char(created_at::date, 'YYYY-MM-DD') AS label, COUNT(*) AS value
                                FROM orders
                                WHERE created_at >= ?
                                GROUP BY created_at::date
                                ORDER BY created_at::date
                                """,
                        since,
                        false
                ),
                startDate,
                endDate,
                true
        ));
        response.setRevenueTrend(buildSeries(
                loadDailyValueMap(
                        """
                                SELECT to_char(created_at::date, 'YYYY-MM-DD') AS label, COALESCE(SUM(amount_eur), 0) AS value
                                FROM orders
                                WHERE created_at >= ?
                                GROUP BY created_at::date
                                ORDER BY created_at::date
                                """,
                        since,
                        true
                ),
                startDate,
                endDate,
                false
        ));
        response.setOrderStatuses(loadBreakdown(
                """
                        SELECT COALESCE(NULLIF(TRIM(status), ''), 'Sin estado') AS label, COUNT(*) AS value
                        FROM orders
                        GROUP BY 1
                        ORDER BY value DESC, label ASC
                        """
        ));
        response.setTopCategories(loadBreakdown(
                """
                        SELECT COALESCE(NULLIF(TRIM(categoria), ''), 'Sin categoría') AS label, COUNT(*) AS value
                        FROM items
                        GROUP BY 1
                        ORDER BY value DESC, label ASC
                        LIMIT 6
                        """
        ));
        response.setUserRoles(loadBreakdown(
                """
                        SELECT COALESCE(NULLIF(TRIM(role), ''), 'user') AS label, COUNT(*) AS value
                        FROM users
                        GROUP BY 1
                        ORDER BY value DESC, label ASC
                        """
        ));

        return response;
    }

    private AdminMetricsResponse.Summary loadSummary(OffsetDateTime since) {
        AdminMetricsResponse.Summary summary = new AdminMetricsResponse.Summary();
        summary.setTotalUsers(queryLong("SELECT COUNT(*) FROM users"));
        summary.setAdminUsers(queryLong("SELECT COUNT(*) FROM users WHERE LOWER(COALESCE(role, 'user')) = 'admin'"));
        summary.setTotalItems(queryLong("SELECT COUNT(*) FROM items"));
        summary.setAvailableItems(queryLong("SELECT COUNT(*) FROM items WHERE available = TRUE"));
        summary.setTotalOrders(queryLong("SELECT COUNT(*) FROM orders"));
        summary.setTotalRevenue(queryDouble("SELECT COALESCE(SUM(amount_eur), 0) FROM orders"));
        summary.setOrdersLast30Days(queryLong("SELECT COUNT(*) FROM orders WHERE created_at >= ?", since));
        summary.setRevenueLast30Days(queryDouble("SELECT COALESCE(SUM(amount_eur), 0) FROM orders WHERE created_at >= ?", since));
        summary.setActiveBuyersLast30Days(queryLong("SELECT COUNT(DISTINCT buyer_id) FROM orders WHERE created_at >= ?", since));
        summary.setActiveSellersLast30Days(queryLong("SELECT COUNT(DISTINCT seller_id) FROM orders WHERE created_at >= ?", since));
        return summary;
    }

    private AdminMetricsResponse.Series buildSeries(Map<LocalDate, Double> valuesByDate, LocalDate startDate, LocalDate endDate, boolean integerValues) {
        AdminMetricsResponse.Series series = new AdminMetricsResponse.Series();
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            labels.add(current.toString());
            double value = valuesByDate.getOrDefault(current, 0.0d);
            values.add(integerValues ? (double) Math.round(value) : roundTwoDecimals(value));
            current = current.plusDays(1);
        }

        series.setLabels(labels);
        series.setValues(values);
        return series;
    }

    private AdminMetricsResponse.Breakdown loadBreakdown(String sql) {
        AdminMetricsResponse.Breakdown breakdown = new AdminMetricsResponse.Breakdown();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            labels.add(String.valueOf(row.get("label")));
            values.add(toLong(row.get("value")));
        }

        breakdown.setLabels(labels);
        breakdown.setValues(values);
        return breakdown;
    }

    private Map<LocalDate, Double> loadDailyValueMap(String sql, OffsetDateTime since, boolean asDouble) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, since);
        Map<LocalDate, Double> values = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            Object label = row.get("label");
            Object value = row.get("value");
            if (label == null || value == null) {
                continue;
            }

            LocalDate date = LocalDate.parse(String.valueOf(label));
            values.put(date, asDouble ? toDouble(value) : (double) toLong(value));
        }

        return values;
    }

    private long queryLong(String sql, Object... args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
        return value == null ? 0L : value;
    }

    private double queryDouble(String sql, Object... args) {
        BigDecimal value = jdbcTemplate.queryForObject(sql, BigDecimal.class, args);
        return value == null ? 0.0d : value.doubleValue();
    }

    private long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String raw && !raw.isBlank()) {
            return Long.parseLong(raw.trim());
        }
        return 0L;
    }

    private double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String raw && !raw.isBlank()) {
            return Double.parseDouble(raw.trim());
        }
        return 0.0d;
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0d) / 100.0d;
    }

    private int normalizeWindowDays(int requestedDays) {
        if (requestedDays <= 0) {
            return DEFAULT_WINDOW_DAYS;
        }

        return Math.max(MIN_WINDOW_DAYS, Math.min(requestedDays, MAX_WINDOW_DAYS));
    }
}