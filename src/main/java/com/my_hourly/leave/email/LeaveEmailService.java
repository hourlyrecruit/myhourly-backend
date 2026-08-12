package com.my_hourly.leave.email;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.request.LeaveActionRequest;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.LeaveAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveEmailService {

    @Value("${hr.email:}")
    private String hrEmail;

    private final JavaMailSender mailSender;

    public void sendLeaveApplicationEmail(
            LeaveRequest leaveRequest) {

        Employee employee =
                leaveRequest.getEmployee();

        Employee manager =
                employee.getReportingManager();

        if (manager == null || manager.getEmail() == null) {
            return;
        }

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(manager.getEmail());

        message.setSubject(
                "New Leave Request - "
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
        );

        message.setText(
                "Hello " + manager.getFirstName() + ",\n\n"
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
                        + " has applied for leave.\n\n"

                        + "Leave Type: "
                        + leaveRequest.getLeaveType().getName()
                        + "\n"

                        + " Employee Email: "
                        + employee.getEmail()
                        + "\n\n"

                        + "Start Date: "
                        + leaveRequest.getStartDate()
                        + "\n"

                        + "End Date: "
                        + leaveRequest.getEndDate()
                        + "\n"

                        + "Total Days: "
                        + leaveRequest.getTotalDays()
                        + "\n"

                        + "Reason: "
                        + leaveRequest.getReason()
                        + "\n\n"

                        + "Please review the leave request in the MyHourly HRMS."
        );

        mailSender.send(message);
    }


    public void sendLeaveCancellationEmail(
            LeaveRequest leaveRequest) {

        Employee employee = leaveRequest.getEmployee();

        Employee manager = employee.getReportingManager();

        if (manager == null || manager.getEmail() == null) {
            log.warn(
                    "No reporting manager/email found for employee ID: {}",
                    employee.getId()
            );
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(manager.getEmail());

        message.setSubject(
                "Leave Request Cancelled - "
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
        );

        message.setText(
                "Hello " + manager.getFirstName() + ",\n\n"
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName()
                        + " has cancelled their leave request.\n\n"

                        + "Leave Type: "
                        + leaveRequest.getLeaveType().getName()
                        + "\n"

                        + "Start Date: "
                        + leaveRequest.getStartDate()
                        + "\n"

                        + "End Date: "
                        + leaveRequest.getEndDate()
                        + "\n"

                        + "Total Days: "
                        + leaveRequest.getTotalDays()
                        + "\n"

                        + "Reason: "
                        + leaveRequest.getReason()
                        + "\n\n"

                        + "Leave Status: CANCELLED\n\n"

                        + "Please check the HRMS for more details."
        );

        mailSender.send(message);
    }

    public void sendManagerLeaveActionEmail(
            LeaveRequest leaveRequest,
            Employee manager,
            LeaveActionRequest request) {

        Employee employee = leaveRequest.getEmployee();

        if (employee == null) {
            log.warn(
                    "Employee not found for leave request ID: {}",
                    leaveRequest.getId()
            );
            return;
        }

        boolean approved =
                request.getAction() == LeaveAction.APPROVE;

        String status =
                approved ? "APPROVED" : "REJECTED";

        String subject =
                "Leave Request " + status
                        + " - "
                        + employee.getFirstName()
                        + " "
                        + employee.getLastName();

        StringBuilder body = new StringBuilder();

        body.append("Hello ")
                .append(employee.getFirstName())
                .append(",\n\n");

        body.append("Your leave request has been ")
                .append(status)
                .append(" by ")
                .append(manager.getFirstName())
                .append(" ")
                .append(manager.getLastName())
                .append(".\n\n");

        body.append("Leave Details:\n");

        body.append("Leave Type: ")
                .append(leaveRequest.getLeaveType().getName())
                .append("\n");

        body.append("Start Date: ")
                .append(leaveRequest.getStartDate())
                .append("\n");

        body.append("End Date: ")
                .append(leaveRequest.getEndDate())
                .append("\n");

        body.append("Total Days: ")
                .append(leaveRequest.getTotalDays())
                .append("\n");

        body.append("Reason: ")
                .append(leaveRequest.getReason())
                .append("\n");

        if (!approved) {
            body.append("\nRejection Reason: ")
                    .append(request.getReason())
                    .append("\n");
        }

        body.append("\nStatus: ")
                .append(status)
                .append("\n\n");

        body.append("Please check the HRMS for more details.");

        String emailBody = body.toString();

        // Send to Employee
        sendLeaveEmail(
                employee.getEmail(),
                subject,
                emailBody
        );

        // Send to Manager
//        if (manager != null &&
//                manager.getEmail() != null &&
//                !manager.getEmail().isBlank()) {
//
//            sendLeaveEmail(
//                    manager.getEmail(),
//                    subject,
//                    emailBody
//            );
//
//        } else {
//            log.warn(
//                    "Manager email not found for leave request ID: {}",
//                    leaveRequest.getId()
//            );
//        }

        // Send to HR
        if (hrEmail != null && !hrEmail.isBlank()) {

            sendLeaveEmail(
                    hrEmail,
                    subject,
                    emailBody
            );

        } else {
            log.warn(
                    "HR email is not configured for leave request ID: {}",
                    leaveRequest.getId()
            );
        }
    }

    private void sendLeaveEmail(
            String email,
            String subject,
            String body) {

        if (email == null || email.isBlank()) {
            log.warn("Cannot send leave email: email address is empty");
            return;
        }

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info(
                    "Leave email sent successfully to {}",
                    email
            );

        } catch (MailException e) {

            log.error(
                    "Failed to send leave email to {}: {}",
                    email,
                    e.getMessage(),
                    e
            );
        }
    }
}