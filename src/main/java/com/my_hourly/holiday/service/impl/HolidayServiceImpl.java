package com.my_hourly.holiday.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;

import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.holiday.api.request.CreateHolidayRequest;
import com.my_hourly.holiday.api.request.UpdateHolidayRequest;
import com.my_hourly.holiday.api.response.HolidayCalendarResponse;
import com.my_hourly.holiday.api.response.HolidayResponse;
import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.entity.HolidayType;
import com.my_hourly.holiday.mapper.HolidayMapper;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.holiday.service.HolidayService;
import com.my_hourly.holiday.specification.HolidaySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;

    @Override
    public HolidayResponse createHoliday(
            CreateHolidayRequest request
    ) {

        if (holidayRepository.existsByHolidayDate(
                request.getHolidayDate()
        )) {

            throw new ValidationException(
                    "Holiday already exists for this date.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        Holiday holiday = holidayMapper.toEntity(request);

        Holiday savedHoliday =
                holidayRepository.save(holiday);

        return holidayMapper.toResponse(savedHoliday);

    }

    @Override
    @Transactional(readOnly = true)
    public HolidayResponse getHolidayById(
            Long holidayId
    ) {

        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Holiday not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return holidayMapper.toResponse(holiday);

    }

    @Override
    public HolidayResponse updateHoliday(
            Long holidayId,
            UpdateHolidayRequest request
    ) {

        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Holiday not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        Holiday existingHoliday =
                holidayRepository.findByHolidayDate(
                        request.getHolidayDate()
                ).orElse(null);

        if (existingHoliday != null &&
                !existingHoliday.getId().equals(holidayId)) {

            throw new ValidationException(
                    "Holiday already exists for this date.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        holidayMapper.updateEntity(
                holiday,
                request
        );

        Holiday updatedHoliday =
                holidayRepository.save(holiday);

        return holidayMapper.toResponse(updatedHoliday);

    }

    @Override
    public void deleteHoliday(
            Long holidayId
    ) {

        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Holiday not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        holiday.setActive(false);

        holidayRepository.save(holiday);

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<HolidayResponse> getAllHolidays(
            int page,
            int size,
            String sortBy,
            String sortDirection,
            String holidayName,
            HolidayType holidayType,
            LocalDate fromDate,
            LocalDate toDate,
            Boolean active
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Holiday> specification = Specification
                .where(HolidaySpecification.holidayNameContains(holidayName))
                .and(HolidaySpecification.hasType(holidayType))
                .and(HolidaySpecification.fromDate(fromDate))
                .and(HolidaySpecification.toDate(toDate))
                .and(HolidaySpecification.isActive(active));

        Page<Holiday> holidayPage =
                holidayRepository.findAll(specification, pageable);

        List<HolidayResponse> responses =
                holidayPage.getContent()
                        .stream()
                        .map(holidayMapper::toResponse)
                        .toList();

        return PageResponse.<HolidayResponse>builder()
                .content(responses)
                .page(holidayPage.getNumber())
                .size(holidayPage.getSize())
                .totalElements(holidayPage.getTotalElements())
                .totalPages(holidayPage.getTotalPages())
                .last(holidayPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HolidayCalendarResponse> getHolidayCalendar(
            Integer month,
            Integer year
    ) {

        LocalDate today = LocalDate.now();

        if (month == null) {
            month = today.getMonthValue();
        }

        if (year == null) {
            year = today.getYear();
        }

        LocalDate startDate = LocalDate.of(year, month, 1);

        LocalDate endDate =
                startDate.withDayOfMonth(startDate.lengthOfMonth());

        return holidayRepository
                .findByHolidayDateBetween(startDate, endDate)
                .stream()
                .map(holidayMapper::toCalendarResponse)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<HolidayResponse> getUpcomingHolidays() {

        return holidayRepository
                .findByHolidayDateGreaterThanEqualAndActiveTrueOrderByHolidayDateAsc(
                        LocalDate.now()
                )
                .stream()
                .map(holidayMapper::toResponse)
                .toList();

    }
}
