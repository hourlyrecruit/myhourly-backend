# Report API - Final Structure

## Overview
The report API has been consolidated into 2 unified GET endpoints that support multiple output formats (JSON, Excel, PDF) via a `format` query parameter.

## API Endpoints

### 1. Attendance Report
```
GET /api/v1/reports/attendance
```

#### Parameters
| Parameter | Type | Required | Default | Description | Example |
|-----------|------|----------|---------|-------------|---------|
| `format` | String | No | `json` | Response format: json, excel, pdf | `json` |
| `employeeId` | Long | No | - | Filter by Employee ID | `1` |
| `employeeName` | String | No | - | Filter by Employee Name (partial, searches firstName & lastName) | `John` |
| `departmentId` | Long | No | - | Filter by Department ID | `5` |
| `attendanceStatus` | Enum | No | - | Filter by Status (PRESENT, ABSENT, LATE, etc.) | `PRESENT` |
| `startDate` | Date | No | - | Start Date (YYYY-MM-DD) | `2026-07-01` |
| `endDate` | Date | No | - | End Date (YYYY-MM-DD) | `2026-07-31` |
| `month` | Integer | No | - | Month (1-12) - auto-converts to date range | `7` |
| `year` | Integer | No | - | Year (2000-2100) | `2026` |
| `page` | Integer | No | `0` | Page number (0-indexed, JSON only) | `0` |
| `size` | Integer | No | `20` | Page size (1-100, JSON only) | `20` |
| `sortBy` | String | No | `attendanceDate` | Sort field | `attendanceDate` |
| `sortDir` | String | No | `DESC` | Sort direction (ASC/DESC) | `DESC` |

#### Valid Sort Fields for Attendance
- `attendanceDate`
- `checkInTime`
- `checkOutTime`
- `employeeName` (not a direct field, may need adjustment)
- `departmentName` (not a direct field, may need adjustment)

#### Usage Examples
```bash
# Get JSON report (default)
GET /api/v1/reports/attendance?month=7&year=2026

# Download Excel
GET /api/v1/reports/attendance?format=excel&month=7&year=2026

# Download PDF
GET /api/v1/reports/attendance?format=pdf&departmentId=5&startDate=2026-07-01&endDate=2026-07-31

# Paginated JSON with filters
GET /api/v1/reports/attendance?employeeId=1&page=0&size=20&sortBy=attendanceDate&sortDir=DESC
```

---

### 2. Leave Report
```
GET /api/v1/reports/leave
```

#### Parameters
| Parameter | Type | Required | Default | Description | Example |
|-----------|------|----------|---------|-------------|---------|
| `format` | String | No | `json` | Response format: json, excel, pdf | `json` |
| `employeeId` | Long | No | - | Filter by Employee ID | `1` |
| `employeeName` | String | No | - | Filter by Employee Name (partial, searches firstName & lastName) | `John` |
| `departmentId` | Long | No | - | Filter by Department ID | `5` |
| `leaveType` | Enum | No | - | Filter by Leave Type | `SICK_LEAVE` |
| `leaveStatus` | Enum | No | - | Filter by Status (APPROVED, PENDING, REJECTED, etc.) | `APPROVED` |
| `startDate` | Date | No | - | Start Date (YYYY-MM-DD) | `2026-07-01` |
| `endDate` | Date | No | - | End Date (YYYY-MM-DD) | `2026-07-31` |
| `month` | Integer | No | - | Month (1-12) - auto-converts to date range | `7` |
| `year` | Integer | No | - | Year (2000-2100) | `2026` |
| `page` | Integer | No | `0` | Page number (0-indexed, JSON only) | `0` |
| `size` | Integer | No | `20` | Page size (1-100, JSON only) | `20` |
| `sortBy` | String | No | `createdAt` | Sort field | `createdAt` |
| `sortDir` | String | No | `DESC` | Sort direction (ASC/DESC) | `DESC` |

#### Valid Sort Fields for Leave
- `createdAt` ⭐ (timestamp when leave was created)
- `updatedAt` (timestamp when leave was last updated)
- `startDate`
- `endDate`
- `totalDays`
- `status`
- `employeeName` (not a direct field, may need adjustment)
- `departmentName` (not a direct field, may need adjustment)

#### Usage Examples
```bash
# Get JSON report (default)
GET /api/v1/reports/leave?month=7&year=2026

# Download Excel with filters
GET /api/v1/reports/leave?format=excel&leaveStatus=APPROVED&departmentId=5

# Download PDF for specific employee
GET /api/v1/reports/leave?format=pdf&employeeId=1&startDate=2026-01-01&endDate=2026-12-31

# Paginated JSON
GET /api/v1/reports/leave?page=0&size=20&sortBy=createdAt&sortDir=DESC
```

---

## Response Formats

### JSON Format (default)
Returns paginated JSON response with content and summary:

```json
{
  "content": [
    {
      "employeeId": 1,
      "employeeCode": "EMP001",
      "employeeName": "John Doe",
      "departmentName": "Engineering",
      "leaveId": 10,
      "leaveType": {...},
      "leaveStatus": "APPROVED",
      "startDate": "2026-07-28",
      "endDate": "2026-07-30",
      "totalDays": 3,
      "reason": "Family vacation",
      "createdAt": "2026-07-20T10:30:00",
      "updatedAt": "2026-07-21T14:00:00"
    }
  ],
  "summary": {
    "totalLeaves": 75,
    "approvedLeaves": 60,
    "pendingLeaves": 10,
    "rejectedLeaves": 3,
    "cancelledLeaves": 2,
    "totalLeaveDays": 225,
    "averageLeaveDays": 3.0,
    "uniqueEmployees": 45
  },
  "page": 0,
  "size": 20,
  "totalElements": 75,
  "totalPages": 4,
  "first": true,
  "last": false,
  "hasNext": true,
  "hasPrevious": false
}
```

### Excel Format (`format=excel`)
Returns binary Excel file (.xlsx) with:
- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Content-Disposition: `attachment; filename=leave-report.xlsx`
- All filtered records (no pagination)

### PDF Format (`format=pdf`)
Returns binary PDF file with:
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename=leave-report.pdf`
- All filtered records (no pagination)

---

## Important Changes Made

### 1. Fixed Sort Field Issues
- **Leave Report**: Changed default sort from `appliedDate` (doesn't exist) to `createdAt` (exists in BaseEntity)
- Updated validation patterns to match actual entity fields

### 2. Updated LeaveReportResponse
- Removed: `appliedDate`, `approvedBy`, `approvedDate` (not in entity)
- Added: `createdAt`, `updatedAt` (from BaseEntity)
- These fields now properly map from the LeaveRequest entity

### 3. Unified API Design
- Single endpoint per report type
- Format specified via query parameter
- Same filters work across all formats
- Cleaner, more RESTful design

### 4. Enhanced Filtering
- Employee name search now checks BOTH firstName AND lastName using OR condition
- All filter combinations work properly

---

## Security
- All endpoints require `HR` or `MANAGER` role via `@PreAuthorize`
- Request validation at parameter level with `@Min`, `@Max`, `@Pattern`

---

## Database Fields Reference

### LeaveRequest Entity
```java
- id (Long)
- employee (Employee)
- leaveType (LeaveType)
- startDate (LocalDate)
- endDate (LocalDate)
- totalDays (Integer)
- reason (String)
- status (LeaveStatus)
- createdAt (LocalDateTime) ← from BaseEntity
- updatedAt (LocalDateTime) ← from BaseEntity
```

### Attendance Entity
```java
- id (Long)
- employee (Employee)
- attendanceDate (LocalDate)
- checkInTime (LocalDateTime)
- checkOutTime (LocalDateTime)
- workingMinutes (Integer)
- totalBreakMinutes (Integer)
- attendanceStatus (AttendanceStatus)
- createdAt (LocalDateTime) ← from BaseEntity
- updatedAt (LocalDateTime) ← from BaseEntity
```

---

## Testing

### Test Valid Sort Fields
```bash
# Valid - works
GET /api/v1/reports/leave?sortBy=createdAt

# Valid - works
GET /api/v1/reports/leave?sortBy=startDate

# Valid - works  
GET /api/v1/reports/attendance?sortBy=attendanceDate

# Invalid - will fail (appliedDate doesn't exist)
GET /api/v1/reports/leave?sortBy=appliedDate
```

### Test Format Parameter
```bash
# All should work with same filters
GET /api/v1/reports/leave?month=7&year=2026&format=json
GET /api/v1/reports/leave?month=7&year=2026&format=excel
GET /api/v1/reports/leave?month=7&year=2026&format=pdf
```

---

## Benefits of Current Design

✅ **Single endpoint per resource** - RESTful design  
✅ **Format as query param** - Easy to use, can bookmark  
✅ **GET method for all** - Proper HTTP semantics  
✅ **Same filters everywhere** - Consistent API  
✅ **Browser-friendly** - Direct download links work  
✅ **No duplicate code** - Single request handling logic  
✅ **Proper validation** - Type-safe parameters  
✅ **Correct field mapping** - No more missing property errors
