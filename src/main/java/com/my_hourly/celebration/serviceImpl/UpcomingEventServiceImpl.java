package com.my_hourly.celebration.serviceImpl;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.celebration.dto.UpcomingEventRequest;
import com.my_hourly.celebration.dto.UpcomingEventResponse;
import com.my_hourly.celebration.entity.UpcomingEvents;
import com.my_hourly.celebration.repository.UpcomingEventRepository;
import com.my_hourly.celebration.service.UpcomingEventService;
import com.my_hourly.celebration.dto.ApiResponse;
import com.my_hourly.employee.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpcomingEventServiceImpl implements UpcomingEventService {

    private final UpcomingEventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository
                .findByUsernameOrEmail(authentication.getName(), authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    private void validateHr() {
        User user = getLoggedInUser();
        if (user.getRole() != RoleName.HR_ADMIN) {
            throw new IllegalStateException("Only HR can perform this action");
        }
    }
    @Override
    public ResponseEntity<ApiResponse> createEvent(UpcomingEventRequest request) {
        User user = getLoggedInUser();

        if (user.getRole() != RoleName.HR_ADMIN) {
            throw new IllegalStateException("Only HR can perform this action");
        }
        UpcomingEvents event = new UpcomingEvents();
        event.setTitle(request.getTitle());
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setCreatedAt(LocalDateTime.now());
        event.setCreatedBy(user.getId());
        UpcomingEvents savedEvent = eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(
                        true,
                        "Upcoming event created successfully",
                        map(savedEvent)));
    }

    @Override
    public List<UpcomingEventResponse> getUpcomingEvents() {

        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        List<UpcomingEventResponse> events = eventRepository
                .findByEventDateBetweenOrderByEventDateAsc(today, nextWeek)
                .stream()
                .map(this::map)
                .toList();

        List<UpcomingEventResponse> response = new ArrayList<>(events);

        employeeRepository.findAll().forEach(employee -> {

            // Birthday
            if (employee.getDateOfBirth() != null) {

                LocalDate birthday = employee.getDateOfBirth().withYear(today.getYear());

                if (birthday.isBefore(today)) {
                    birthday = birthday.plusYears(1);
                }

                if (!birthday.isBefore(today) && !birthday.isAfter(nextWeek)) {

                    UpcomingEventResponse birthdayEvent = new UpcomingEventResponse();
                    birthdayEvent.setTitle(employee.getFirstName() + " " + employee.getLastName() + "'s Birthday");
                    birthdayEvent.setDescription("Birthday Celebration");
                    birthdayEvent.setLocation("Office");
                    birthdayEvent.setEventDate(birthday);

                    response.add(birthdayEvent);
                }
            }

            // Work Anniversary
            if (employee.getDateOfJoining() != null) {

                LocalDate anniversary = employee.getDateOfJoining().withYear(today.getYear());

                if (anniversary.isBefore(today)) {
                    anniversary = anniversary.plusYears(1);
                }

                if (!anniversary.isBefore(today) && !anniversary.isAfter(nextWeek)) {

                    UpcomingEventResponse anniversaryEvent = new UpcomingEventResponse();
                    anniversaryEvent.setTitle(employee.getFirstName() + " " + employee.getLastName() + "'s Work Anniversary");
                    anniversaryEvent.setDescription("Work Anniversary Celebration");
                    anniversaryEvent.setLocation("Office");
                    anniversaryEvent.setEventDate(anniversary);

                    response.add(anniversaryEvent);
                }
            }
        });

        response.sort(Comparator.comparing(UpcomingEventResponse::getEventDate));

        return response;
    }

    @Override
    public UpcomingEventResponse getEventById(Long id) {
        UpcomingEvents event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Upcoming event not found"));
        return map(event);
    }

    @Override
    public ResponseEntity<ApiResponse> updateEvent(Long id, UpcomingEventRequest request) {
        validateHr();
        UpcomingEvents event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Upcoming event not found"));
        event.setTitle(request.getTitle());
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        UpcomingEvents updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Upcoming event updated successfully",
                        map(updatedEvent)));
    }
    @Override
    public ResponseEntity<ApiResponse> patchEvent(Long id, UpcomingEventRequest request) {
        validateHr();
        UpcomingEvents event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Upcoming event not found"));
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        UpcomingEvents updatedEvent = eventRepository.save(event);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Upcoming event updated successfully",
                        map(updatedEvent)));
    }
    @Override
    public ResponseEntity<ApiResponse> deleteEvent(Long id) {
        validateHr();
        UpcomingEvents event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Upcoming event not found"));
        UpcomingEventResponse response = map(event);
        eventRepository.delete(event);
        return ResponseEntity.ok(
                new ApiResponse(
                        true,
                        "Upcoming event deleted successfully",
                        response));
    }

    private UpcomingEventResponse map(UpcomingEvents event) {

        UpcomingEventResponse response = new UpcomingEventResponse();

        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setLocation(event.getLocation());
        response.setDescription(event.getDescription());
        response.setEventDate(event.getEventDate());

        return response;
    }
}
