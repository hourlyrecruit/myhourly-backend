package com.my_hourly.settings.notification.entity;

// DISABLED: The whole notification settings submodule is unused.
// - No business module reads NotificationSettings or any of its flags
//   (notification behavior is currently hard-coded in the notification module).
// - Its only consumers were SettingController (endpoints now disabled) and
//   DataInitializer.seedNotificationSettings() (also disabled).
// - CompanySettings was kept, but NotificationSettings was disabled per review.
// Re-enable the submodule here and in SettingController / DataInitializer if needed.
//
//import com.my_hourly.settings.BaseSettings;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//import lombok.*;
//
//@Entity
//@Table(name = "notification_settings")
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class NotificationSettings extends BaseSettings {
//
//    @Column(nullable = false)
//    private Boolean emailNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean inAppNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean attendanceNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean leaveNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean workLogNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean holidayNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean birthdayNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean announcementNotificationsEnabled;
//
//    @Column(nullable = false)
//    private Boolean notifyManagers;
//
//    @Column(nullable = false)
//    private Boolean notifyEmployees;
//
//    // NOTE: removed — duplicate `active` field shadowed the one inherited from
//    // BaseSettings (this.active vs super.active ambiguity). The inherited
//    // BaseSettings#active is the column actually used.
////    @Column(nullable = false)
////    private Boolean active;
//}
