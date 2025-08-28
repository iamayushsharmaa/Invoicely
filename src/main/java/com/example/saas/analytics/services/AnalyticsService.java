package com.example.saas.analytics.services;

import com.example.saas.invoices.models.Invoice;
import com.example.saas.analytics.dto.DashboardOverviewDto;
import com.example.saas.analytics.dto.TimeRevenueDto;
import com.example.saas.analytics.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public DashboardOverviewDto getOverview(UUID userId) {
        long total = analyticsRepository.countByUser_Id(userId);
        long paid = analyticsRepository.countByUser_IdAndStatus(userId, "PAID");
        long unpaid = total - paid;
        BigDecimal revenue = analyticsRepository.getTotalRevenue(userId);
        LocalDate lastDate = analyticsRepository.getLastInvoiceDate(userId);
        LocalDate nextDue = analyticsRepository.getNextDueDate(userId);

        return new DashboardOverviewDto(total, paid, unpaid, revenue, lastDate, nextDue);
    }

    public List<TimeRevenueDto> getRevenue(UUID userId, String type, Integer year, Integer month) {
        switch (type.toLowerCase()) {
            case "week":
                return getRevenueThisWeek(userId);
            case "month":
                if (year == null || month == null)
                    throw new IllegalArgumentException("Year and month are required for monthly revenue");
                return getRevenueThisMonth(userId, year, month);

            case "year":
                if (year == null)
                    throw new IllegalArgumentException("Year is required for yearly revenue");
                return getRevenueThisYear(userId, year);
            default:
                throw new IllegalArgumentException("Invalid type: must be week, month, or year");
        }
    }


    public List<TimeRevenueDto> getRevenueThisWeek(UUID userId) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);

        List<Invoice> invoices = analyticsRepository.findByUser_IdAndInvoiceDateBetween(userId, start, today);

        Map<LocalDate, BigDecimal> revenueMap = invoices.stream()
                .collect(Collectors.groupingBy(
                        Invoice::getInvoiceDate,
                        TreeMap::new,
                        Collectors.mapping(Invoice::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return revenueMap.entrySet().stream()
                .map(entry -> new TimeRevenueDto(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<TimeRevenueDto> getRevenueThisMonth(UUID userId, Integer year, Integer month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Invoice> invoices = analyticsRepository.findByUser_IdAndInvoiceDateBetween(userId, start, end);

        Map<Integer, BigDecimal> revenueMap = invoices.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getInvoiceDate().getDayOfMonth(),
                        TreeMap::new,
                        Collectors.mapping(Invoice::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return revenueMap.entrySet().stream()
                .map(e -> new TimeRevenueDto(String.format("%02d", e.getKey()), e.getValue())) // "01", "02", ...
                .collect(Collectors.toList());
    }

    public List<TimeRevenueDto> getRevenueThisYear(UUID userId, Integer year) {

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Invoice> invoices = analyticsRepository.findByUser_IdAndInvoiceDateBetween(userId, start, end);

        Map<Month, BigDecimal> revenueMap = invoices.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getInvoiceDate().getMonth(),
                        TreeMap::new,
                        Collectors.mapping(Invoice::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return revenueMap.entrySet().stream()
                .map(e -> new TimeRevenueDto(e.getKey().getDisplayName(TextStyle.SHORT, Locale.ENGLISH), e.getValue()))
                .collect(Collectors.toList());
    }
}
