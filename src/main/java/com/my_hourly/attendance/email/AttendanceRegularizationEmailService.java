package com.my_hourly.attendance.email;

import com.my_hourly.attendance.entity.AttendanceRegularization;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceRegularizationEmailService {
    @Value("${hr.email:}")
    private String hrEmail;

    private final JavaMailSender mailSender;



    public void sendAttendanceRegularizationRequestEmail(
            AttendanceRegularization attendanceRegularization) {

        Employee employee =
                attendanceRegularization.getEmployee();

        Employee manager =
                employee.getReportingManager();

        if (manager == null || manager.getEmail() == null) {
            return;
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(manager.getEmail());

        message.setSubject(
                "New Attendance Regularization Request - "
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
        );

        message.setText(
                "Hello " + manager.getFirstName() + ",\n\n"
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
                        + " has applied for attendance regularization.\n\n"

                        + "Requested on Date: "
                        + attendanceRegularization.getRequestedAt()
                        + "\n"

                        + " Employee Email: "
                        + employee.getEmail()
                        + "\n\n"

                        + "From Date: "
                        + attendanceRegularization.getFromDate()
                        + "\n"

                        + "To Date: "
                        + attendanceRegularization.getToDate()
                        + "\n"

                        + "Reason: "
                        + attendanceRegularization.getReason()
                        + "\n\n"

                        + "Please review the attendance regularization request in the MyHourly HRMS."
        );

        mailSender.send(message);
    }

    public void sendAttendanceRegularizationStatusUpdateEmail(
            AttendanceRegularization attendanceRegularization) {

        Employee employee =
                attendanceRegularization.getEmployee();

        if (employee == null || employee.getEmail() == null) {
            return;
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(employee.getEmail(), hrEmail);

        message.setSubject(
                "Attendance Regularization Status Update - "
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
        );

        message.setText(
                "Hello " + employee.getFirstName() + ",\n\n"
                        + "Your attendance regularization request has been updated.\n\n"

                        + "Status: "
                        + attendanceRegularization.getStatus()
                        + "\n"

                        + "Requested on Date: "
                        + attendanceRegularization.getRequestedAt()
                        + "\n"

                        + "From Date: "
                        + attendanceRegularization.getFromDate()
                        + "\n"

                        + "To Date: "
                        + attendanceRegularization.getToDate()
                        + "\n"

                        + "Updated By Manager: "
                        + attendanceRegularization.getApprovedBy().getFirstName()
                        + "\n\n"

                        + "The updated details are now reflected in your attendance records.\n\n"

                        + "You can view the complete details of this regularization request in the MyHourly HRMS."
                        + "\n"
                        + "If you have any questions, please contact the Manager at " + employee.getReportingManager().getEmail() + ".\n\n"
                        + "Thank you for using MyHourly HRMS."
        );

        mailSender.send(message);
    }
}

