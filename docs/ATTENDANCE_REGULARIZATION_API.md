# Attendance Regularization Module — Frontend Implementation Guide

> **Version:** 1.0  
> **Last Updated:** August 31, 2026  
> **Base URL:** `/api/v1`  
> **Authentication:** Bearer Token (JWT)

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Role-Based Access](#role-based-access)
4. [Enums Reference](#enums-reference)
5. [API Endpoints](#api-endpoints)
   - [5.1 Create Regularization Request](#51-create-regularization-request)
   - [5.2 Get My Requests](#52-get-my-requests)
   - [5.3 Get Pending Requests (Manager)](#53-get-pending-requests-manager)
   - [5.4 Get All Requests (Manager)](#54-get-all-requests-manager)
   - [5.5 Get Regularization Details](#55-get-regularization-details)
   - [5.6 Approve/Reject a Detail](#56-approvereject-a-detail)
   - [5.7 Revert a Detail (HR Admin)](#57-revert-a-detail-hr-admin)
6. [Standard Response Format](#standard-response-format)
7. [Error Codes](#error-codes)
8. [Status Flow Diagrams](#status-flow-diagrams)
9. [Frontend Implementation Recommendations](#frontend-implementation-recommendations)
   - [9.1 Screens & Components](#91-screens--components)
   - [9.2 State Management](#92-state-management)
   - [9.3 Validation Rules](#93-validation-rules)
   - [9.4 UI/UX Suggestions](#94-uiux-suggestions)

---

## 1. Overview

The **Attendance Regularization Module** allows employees to request corrections for their attendance records (e.g., marking an ABSENT day as PRESENT after a valid reason). Managers can approve or reject these requests, and HR Admins can revert previously approved regularizations.

### Key Concepts

- **Regularization Request** — A parent request created by an employee for one or more attendance records within a date range.
- **Regularization Detail** — An individual attendance record within a regularization request. Each detail captures the **original state** (snapshot at request time) and the **requested change**.
- **Approval Workflow** — Each detail is individually approved/rejected by the manager. The parent request status auto-updates based on detail outcomes.

---

## 2. Authentication

All API calls require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

The token is obtained from the existing auth/login endpoint.

---

## 3. Role-Based Access

| Role | Can Do |
|------|--------|
| **EMPLOYEE** | Create requests, view own requests |
| **MANAGER** | View pending requests for subordinates, approve/reject details |
| **HR_ADMIN** | Same as MANAGER + revert approved details |
| **SUPER_ADMIN** | Same as HR_ADMIN |

---

## 4. Enums Reference

### `AttendanceStatus` (existing — used in attendance records)

| Value | Description |
|-------|-------------|
| `PRESENT` | Employee was present |
| `LATE` | Employee arrived late |
| `HALF_DAY` | Half-day attendance |
| `ABSENT` | Employee was absent |
| `LEAVE` | On approved leave |
| `HOLIDAY` | Company holiday |
| `WEEKEND` | Weekend |
| `MISSED_CHECKOUT` | Present but forgot to check out |

### `RegularizationStatus` (parent request status)

| Value | Description |
|-------|-------------|
| `PENDING` | All details are pending |
| `PARTIALLY_APPROVED` | Some details approved, some still pending |
| `APPROVED` | All details approved |
| `REJECTED` | All details rejected |
| `CANCELLED` | Request cancelled (reserved for future use) |

### `RegularizationDetailStatus` (individual detail status)

| Value | Description |
|-------|-------------|
| `PENDING` | Awaiting manager action |
| `APPROVED` | Manager approved the change |
| `REJECTED` | Manager rejected the change |
| `REVERTED` | Previously approved change was reverted by HR |

---

## 5. API Endpoints

### 5.1 Create Regularization Request

**Create a new regularization request for one or more attendance records.**

```
POST /api/v1/attendance-regularizations
```

**Access:** EMPLOYEE, MANAGER, HR_ADMIN, SUPER_ADMIN

#### Request Body

```json
{
  "fromDate": "2026-08-01",
  "toDate": "2026-08-05",
  "reason": "I was present on these days but my attendance was not recorded due to biometric device malfunction.",
  "details": [
    {
      "attendanceId": 101,
      "requestedStatus": "PRESENT"
    },
    {
      "attendanceId": 102,
      "requestedStatus": "PRESENT"
    },
    {
      "attendanceId": 103,
      "requestedStatus": "PRESENT"
    }
  ]
}
```

#### Field Requirements

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `fromDate` | `string (date)` | ✅ | Start date of regularization period (YYYY-MM-DD) |
| `toDate` | `string (date)` | ✅ | End date of regularization period (YYYY-MM-DD) |
| `reason` | `string` | ✅ | Reason for regularization (non-empty) |
| `details` | `array` | ✅ | At least one detail item |
| `details[].attendanceId` | `number (long)` | ✅ | ID of the attendance record to regularize |
| `details[].requestedStatus` | `AttendanceStatus` | ✅ | The status you want to change to |

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Regularization request created successfully.",
  "data": {
    "id": 1,
    "employeeId": 5,
    "employeeName": "John Doe",
    "fromDate": "2026-08-01",
    "toDate": "2026-08-05",
    "reason": "I was present on these days but my attendance was not recorded due to biometric device malfunction.",
    "status": "PENDING",
    "requestedAt": "2026-08-31T10:30:00",
    "approvedAt": null,
    "approvedById": null,
    "approvedByName": null,
    "rejectedAt": null,
    "rejectedById": null,
    "rejectedByName": null,
    "rejectionReason": null,
    "createdAt": "2026-08-31T10:30:00",
    "details": [
      {
        "id": 1,
        "regularizationId": 1,
        "attendanceId": 101,
        "attendanceDate": "2026-08-01",
        "originalStatus": "ABSENT",
        "originalCheckIn": null,
        "originalCheckOut": null,
        "requestedStatus": "PRESENT",
        "requestedCheckIn": null,
        "requestedCheckOut": null,
        "approvedStatus": null,
        "approvedCheckIn": null,
        "approvedCheckOut": null,
        "status": "PENDING",
        "remarks": null
      },
      {
        "id": 2,
        "regularizationId": 1,
        "attendanceId": 102,
        "attendanceDate": "2026-08-02",
        "originalStatus": "LATE",
        "originalCheckIn": "10:30",
        "originalCheckOut": "18:00",
        "requestedStatus": "PRESENT",
        "requestedCheckIn": "10:30",
        "requestedCheckOut": "18:00",
        "approvedStatus": null,
        "approvedCheckIn": null,
        "approvedCheckOut": null,
        "status": "PENDING",
        "remarks": null
      },
      {
        "id": 3,
        "regularizationId": 1,
        "attendanceId": 103,
        "attendanceDate": "2026-08-03",
        "originalStatus": "MISSED_CHECKOUT",
        "originalCheckIn": "09:00",
        "originalCheckOut": null,
        "requestedStatus": "PRESENT",
        "requestedCheckIn": "09:00",
        "requestedCheckOut": "18:00",
        "approvedStatus": null,
        "approvedCheckIn": null,
        "approvedCheckOut": null,
        "status": "PENDING",
        "remarks": null
      }
    ]
  },
  "timestamp": "2026-08-31T10:30:00"
}
```

#### Error Responses

| HTTP Code | Message | When |
|-----------|---------|------|
| 400 | `Employee ID is required.` | `employeeId` missing from token resolution |
| 400 | `At least one detail is required.` | Empty details array |
| 400 | `From date must be before or equal to to date.` | Invalid date range |
| 400 | `Attendance record not found for ID: {id}` | Invalid attendanceId |
| 400 | `Attendance record {id} does not belong to employee {empId}` | Attendance belongs to another employee |
| 400 | `Active regularization request already exists for attendance record {id}` | Duplicate request for same attendance |
| 400 | `Invalid status transition from {current} to {requested}. Allowed: {allowed}` | Invalid status change |

---

### 5.2 Get My Requests

**Retrieve all regularization requests submitted by the logged-in employee.**

```
GET /api/v1/attendance-regularizations/my
```

**Access:** EMPLOYEE, MANAGER, HR_ADMIN, SUPER_ADMIN

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Regularization requests fetched successfully.",
  "data": [
    {
      "id": 1,
      "employeeId": 5,
      "employeeName": "John Doe",
      "fromDate": "2026-08-01",
      "toDate": "2026-08-05",
      "reason": "Biometric device malfunction",
      "status": "PARTIALLY_APPROVED",
      "requestedAt": "2026-08-31T10:30:00",
      "approvedAt": null,
      "approvedById": null,
      "approvedByName": null,
      "rejectedAt": null,
      "rejectedById": null,
      "rejectedByName": null,
      "rejectionReason": null,
      "createdAt": "2026-08-31T10:30:00",
      "details": [
        {
          "id": 1,
          "regularizationId": 1,
          "attendanceId": 101,
          "attendanceDate": "2026-08-01",
          "originalStatus": "ABSENT",
          "originalCheckIn": null,
          "originalCheckOut": null,
          "requestedStatus": "PRESENT",
          "requestedCheckIn": null,
          "requestedCheckOut": null,
          "approvedStatus": "PRESENT",
          "approvedCheckIn": null,
          "approvedCheckOut": null,
          "status": "APPROVED",
          "remarks": "Verified with security logs"
        },
        {
          "id": 2,
          "regularizationId": 1,
          "attendanceId": 102,
          "attendanceDate": "2026-08-02",
          "originalStatus": "LATE",
          "originalCheckIn": "10:30",
          "originalCheckOut": "18:00",
          "requestedStatus": "PRESENT",
          "requestedCheckIn": "10:30",
          "requestedCheckOut": "18:00",
          "approvedStatus": null,
          "approvedCheckIn": null,
          "approvedCheckOut": null,
          "status": "PENDING",
          "remarks": null
        }
      ]
    }
  ],
  "timestamp": "2026-08-31T12:00:00"
}
```

**Returns:** A list of `RegularizationResponse` objects for the current employee, ordered by creation date (newest first).

---

### 5.3 Get Pending Requests (Manager)

**Retrieve regularization requests pending approval for the manager's direct subordinates.**

```
GET /api/v1/attendance-regularizations/pending
```

**Access:** MANAGER, HR_ADMIN, SUPER_ADMIN

#### Response

Same format as "Get My Requests" — returns a `List<RegularizationResponse>` filtered to requests where:
- The request belongs to a direct subordinate of the logged-in manager
- The request status is `PENDING` or `PARTIALLY_APPROVED`

---

### 5.4 Get All Requests (Manager)

**Retrieve all regularization requests for the manager's subordinates (any status).**

```
GET /api/v1/attendance-regularizations/all
```

**Access:** MANAGER, HR_ADMIN, SUPER_ADMIN

#### Response

Same format as "Get My Requests" — returns a `List<RegularizationResponse>` for all direct subordinates, including pending, approved, rejected, and partially approved requests.

---

### 5.5 Get Regularization Details

**Retrieve a single regularization request by its ID with all details.**

```
GET /api/v1/attendance-regularizations/{id}
```

**Access:** EMPLOYEE, MANAGER, HR_ADMIN, SUPER_ADMIN

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | `number (long)` | Regularization request ID |

#### Response (200 OK)

Returns a single `RegularizationResponse` object with full details.

#### Error Responses

| HTTP Code | Message |
|-----------|---------|
| 404 | `Regularization request not found with ID: {id}` |

---

### 5.6 Approve/Reject a Detail

**Approve or reject an individual regularization detail within a request.**

```
PATCH /api/v1/attendance-regularizations/{regularizationId}/details/{detailId}
```

**Access:** MANAGER, HR_ADMIN, SUPER_ADMIN

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `regularizationId` | `number (long)` | Parent regularization request ID |
| `detailId` | `number (long)` | Detail line item ID |

#### Request Body — Approve

```json
{
  "status": "APPROVED",
  "approvedStatus": "PRESENT",
  "remarks": "Verified with building access logs"
}
```

#### Request Body — Reject

```json
{
  "status": "REJECTED",
  "approvedStatus": null,
  "remarks": "Unable to verify. Please provide supporting documents."
}
```

#### Field Requirements

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `status` | `RegularizationDetailStatus` | ✅ | Must be `APPROVED` or `REJECTED` |
| `approvedStatus` | `AttendanceStatus` | ⚠️ Conditional | The final status to apply to attendance. Required when approving, can be null when rejecting. |
| `remarks` | `string` | ❌ | Manager's comments |

> **Important:** The `approvedStatus` must be the same as or a valid upgrade from the `requestedStatus`. Typically it will match the requested status.

#### Response (200 OK)

Returns the full updated `RegularizationResponse` with all details (the parent status auto-updates based on detail outcomes).

#### Error Responses

| HTTP Code | Message | When |
|-----------|---------|------|
| 400 | `Invalid action. Only APPROVED or REJECTED is allowed.` | Status is not APPROVED or REJECTED |
| 400 | `Regularization detail not found with ID: {detailId}` | Invalid detail ID |
| 400 | `Detail does not belong to regularization request {id}` | Mismatched IDs |
| 400 | `Detail is not in PENDING status. Current status: {status}` | Already processed |
| 400 | `Only the employee's reporting manager can take action.` | Unauthorized manager |
| 400 | `Attendance state has changed since the request was created. Current: {status}` | Attendance modified after request |
| 400 | `Invalid approved status transition from {original} to {approved}.` | Invalid status upgrade |

---

### 5.7 Revert a Detail (HR Admin)

**Revert a previously approved regularization detail back to its original attendance state.**

```
POST /api/v1/attendance-regularizations/{regularizationId}/details/{detailId}/revert
```

**Access:** HR_ADMIN, SUPER_ADMIN

#### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `regularizationId` | `number (long)` | Parent regularization request ID |
| `detailId` | `number (long)` | Detail line item ID |

#### Request Body

No body required.

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Regularization detail reverted successfully.",
  "data": "Reverted to original attendance state.",
  "timestamp": "2026-08-31T14:00:00"
}
```

#### What Revert Does

1. Restores the attendance record to its **original snapshot** (status, check-in, check-out at the time the request was created)
2. Changes the detail status from `APPROVED` → `REVERTED`
3. Automatically recalculates the parent request status

#### Error Responses

| HTTP Code | Message | When |
|-----------|---------|------|
| 400 | `Detail is not in APPROVED status. Current status: {status}` | Only approved details can be reverted |
| 400 | `Cannot revert: another active regularization request exists for attendance record {id}` | Another pending request covers the same attendance |

---

## 6. Standard Response Format

All endpoints return responses wrapped in a standard envelope:

```typescript
interface ApiResponse<T> {
  success: boolean;       // true if request succeeded
  message: string;        // Human-readable message
  data: T;                // The actual response data
  timestamp: string;      // ISO 8601 timestamp of when the response was generated
}
```

---

## 7. Error Codes

Backend-specific error codes for the regularization module:

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `REG_001` | 400 | Employee ID is required |
| `REG_002` | 400 | At least one detail is required |
| `REG_003` | 400 | From date must be before or equal to to date |
| `REG_004` | 404 | Regularization request not found |
| `REG_005` | 400 | Active regularization request already exists for attendance record |
| `REG_006` | 400 | Attendance record not found |
| `REG_007` | 400 | Invalid status transition |
| `REG_008` | 400 | Only the reporting manager can take action |
| `REG_009` | 400 | Attendance state has changed since request |
| `REG_010` | 400 | Detail is not in pending status |
| `REG_011` | 400 | Detail is not in approved status (for revert) |
| `REG_012` | 400 | Another active regularization exists (blocks revert) |

---

## 8. Status Flow Diagrams

### Regularization Request (Parent) Status Flow

```
                    ┌──────────┐
                    │  PENDING  │
                    └─────┬─────┘
                          │
              ┌───────────┼───────────┐
              ▼           ▼           ▼
       ┌──────────┐ ┌────────────────┐ ┌──────────┐
       │ APPROVED │ │   PARTIALLY    │ │ REJECTED │
       └──────────┘ │   _APPROVED    │ └──────────┘
                    └────────┬───────┘
                             │
                    ┌────────┼────────┐
                    ▼        ▼        ▼
              ┌──────────┐ ┌──────────┐
              │ APPROVED │ │ REJECTED │
              └──────────┘ └──────────┘
```

**Rules:**
- Starts as `PENDING` when created
- Auto-changes to `PARTIALLY_APPROVED` when at least one detail is approved but others are still pending
- Auto-changes to `APPROVED` when all details are approved
- Auto-changes to `REJECTED` when all details are rejected

### Regularization Detail Status Flow

```
  ┌──────────┐         ┌──────────┐
  │  PENDING  │───────▶│ APPROVED │───────▶  ┌──────────┐
  └──────────┘  approve └──────────┘  revert  │ REVERTED │
       │                                       └──────────┘
       │ reject
       ▼
  ┌──────────┐
  │ REJECTED │
  └──────────┘
```

**Valid Status Transitions (for AttendanceStatus):**

| Original Status | Allowed Requested Status |
|-----------------|-------------------------|
| `LATE` | `PRESENT` |
| `HALF_DAY` | `PRESENT` |
| `ABSENT` | `PRESENT` |
| `MISSED_CHECKOUT` | `PRESENT` |
| `PRESENT` | ❌ (already present — no change needed) |
| `LEAVE` | ❌ (not changeable via regularization) |
| `HOLIDAY` | ❌ (not changeable via regularization) |
| `WEEKEND` | ❌ (not changeable via regularization) |

---

## 9. Frontend Implementation Recommendations

### 9.1 Screens & Components

#### Screen 1: Employee — My Regularization Requests

| Element | Description |
|---------|-------------|
| **Request List Table** | Columns: Date Range, Reason, Status (badge), # Details, Created At, Action (View) |
| **Status Filter** | Tab/toggle to filter by PENDING, PARTIALLY_APPROVED, APPROVED, REJECTED |
| **Create Button** | Opens the "Create Regularization" form/modal |
| **Detail Drawer/Modal** | Shows full request details when "View" is clicked |

**API Calls:**
- `GET /my` — Load the employee's regularization requests
- `GET /{id}` — Load details when a request is clicked

---

#### Screen 2: Employee — Create Regularization Request

| Element | Description |
|---------|-------------|
| **Date Range Picker** | From date and To date (or single range picker) |
| **Attendance List** | Auto-fetched attendance records within the selected date range. Display as a table with checkboxes. |
| **Status Dropdown** | For each selected row, dropdown to choose the requested status (only allowed transitions shown) |
| **Reason Textarea** | Free-text reason (required) |
| **Submit Button** | Calls `POST /` endpoint |

**Workflow:**
1. User selects a date range
2. Frontend fetches attendance records for that employee and date range (from the existing attendance module API)
3. User checks the rows they want to regularize and selects the target status
4. User enters a reason
5. Frontend sends `POST /api/v1/attendance-regularizations`

**API Calls:**
- `GET` existing attendance API (for the employee's records in the date range) — *use existing attendance endpoints*
- `POST /` — Submit the regularization request

---

#### Screen 3: Manager — Pending Regularization Requests

| Element | Description |
|---------|-------------|
| **Request List Table** | Columns: Employee Name, Date Range, Reason, Status, # Pending Details, Created At, Action |
| **Employee Filter** | Dropdown to filter by subordinate name |
| **Action Buttons** | "Review" to open detail view |

---

#### Screen 4: Manager — Review & Approve/Reject

| Element | Description |
|---------|-------------|
| **Request Header** | Employee name, date range, reason, overall status |
| **Details Table** | Columns: Date, Original Status, Original In/Out, Requested Status, Requested In/Out, Status, Action |
| **Per-Detail Actions** | |
| — Approve | Button → Opens a small form to confirm `approvedStatus` and add remarks |
| — Reject | Button → Opens a form to add rejection remarks (required) |
| **Bulk Actions (optional)** | "Approve All" / "Reject All" buttons |

**API Calls:**
- `GET /pending` — Load pending requests
- `GET /{id}` — Load detail view
- `PATCH /{regId}/details/{detailId}` — Approve or reject individual details

---

#### Screen 5: HR Admin — Revert Approval

Same as the Manager review screen, but for `APPROVED` details, shows a "Revert" button instead of Approve/Reject.

**API Calls:**
- `GET /all` — Load all requests
- `POST /{regId}/details/{detailId}/revert` — Revert an approved detail

---

### 9.2 State Management

Recommended state structure (Redux/Zustand/Context):

```typescript
// Regularization state
interface RegularizationState {
  myRequests: RegularizationResponse[];
  pendingRequests: RegularizationResponse[];
  allRequests: RegularizationResponse[];
  selectedRequest: RegularizationResponse | null;
  loading: boolean;
  error: string | null;
}
```

### 9.3 Validation Rules

**Client-Side Validation (Create Form):**

| Rule | Message |
|------|---------|
| From date is required | "Please select a start date" |
| To date is required | "Please select an end date" |
| From date ≤ To date | "Start date must be before or equal to end date" |
| At least one detail selected | "Please select at least one attendance record" |
| Reason is not empty | "Please provide a reason" |
| Each detail has a valid requested status | "Please select a target status for each record" |

**Client-Side Validation (Approve/Reject Form):**

| Rule | Message |
|------|---------|
| Status is APPROVED or REJECTED | "Please select approve or reject" |
| If APPROVED: approvedStatus is required | "Please select the final attendance status" |
| If REJECTED: remarks recommended | "Please add a reason for rejection" |

### 9.4 UI/UX Suggestions

#### Status Badges

Use color-coded badges for statuses:

| Status | Color | Icon |
|--------|-------|------|
| `PENDING` | 🟡 Yellow/Amber | Clock icon |
| `PARTIALLY_APPROVED` | 🔵 Blue | Partial checkmark |
| `APPROVED` | 🟢 Green | Checkmark |
| `REJECTED` | 🔴 Red | X mark |
| `REVERTED` | 🟠 Orange | Undo icon |
| `CANCELLED` | ⚫ Grey | Cancel icon |

#### Request Card (Employee View)

```
┌──────────────────────────────────────────────────────────────┐
│  📋 Regularization Request #1                        PENDING │
│                                                              │
│  📅 Aug 01, 2026 — Aug 05, 2026                             │
│  📝 Biometric device malfunction                             │
│  📎 3 attendance records     ⏰ Submitted 2 hours ago         │
│                                                              │
│  Details:  ✅ 1 Approved  ⏳ 2 Pending  ❌ 0 Rejected        │
│                                                              │
│                                          [View Details →]    │
└──────────────────────────────────────────────────────────────┘
```

#### Detail Row (Manager View)

```
┌───────────────────────────────────────────────────────────────────┐
│  📅 Aug 01, 2026                                                  │
│                                                                   │
│  Original:  ❌ ABSENT  |  Check-in: --   |  Check-out: --         │
│  Request:   ✅ PRESENT |  Check-in: --   |  Check-out: --         │
│                                                                   │
│  [ ✅ Approve ]    [ ❌ Reject ]                                   │
└───────────────────────────────────────────────────────────────────┘
```

#### Date Format

All dates should be displayed in a user-friendly format:
- **Display:** `MMM DD, YYYY` (e.g., "Aug 01, 2026")
- **Time:** `hh:mm A` (e.g., "10:30 AM")
- **API format:** `YYYY-MM-DD` (dates) / ISO 8601 (timestamps)

---

## Appendix: TypeScript Types

```typescript
// Enums
type AttendanceStatus = 
  | 'PRESENT' | 'LATE' | 'HALF_DAY' | 'ABSENT' 
  | 'LEAVE' | 'HOLIDAY' | 'WEEKEND' | 'MISSED_CHECKOUT';

type RegularizationStatus = 
  | 'PENDING' | 'PARTIALLY_APPROVED' | 'APPROVED' 
  | 'REJECTED' | 'CANCELLED';

type RegularizationDetailStatus = 
  | 'PENDING' | 'APPROVED' | 'REJECTED' | 'REVERTED';

// Request Types
interface CreateRegularizationRequest {
  fromDate: string;          // YYYY-MM-DD
  toDate: string;            // YYYY-MM-DD
  reason: string;
  details: CreateRegularizationDetailRequest[];
}

interface CreateRegularizationDetailRequest {
  attendanceId: number;
  requestedStatus: AttendanceStatus;
}

interface RegularizationDetailActionRequest {
  status: 'APPROVED' | 'REJECTED';
  approvedStatus: AttendanceStatus | null;
  remarks?: string;
}

// Response Types
interface RegularizationResponse {
  id: number;
  employeeId: number;
  employeeName: string;
  fromDate: string;
  toDate: string;
  reason: string;
  status: RegularizationStatus;
  requestedAt: string;       // ISO 8601
  approvedAt: string | null;
  approvedById: number | null;
  approvedByName: string | null;
  rejectedAt: string | null;
  rejectedById: number | null;
  rejectedByName: string | null;
  rejectionReason: string | null;
  createdAt: string;
  details: RegularizationDetailResponse[];
}

interface RegularizationDetailResponse {
  id: number;
  regularizationId: number;
  attendanceId: number;
  attendanceDate: string;
  originalStatus: AttendanceStatus;
  originalCheckIn: string | null;
  originalCheckOut: string | null;
  requestedStatus: AttendanceStatus;
  requestedCheckIn: string | null;
  requestedCheckOut: string | null;
  approvedStatus: AttendanceStatus | null;
  approvedCheckIn: string | null;
  approvedCheckOut: string | null;
  status: RegularizationDetailStatus;
  remarks: string | null;
}

// Standard API Response
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
```

---

## Appendix: Sample API Call Sequence

### Employee Creates a Regularization Request

```
1. Employee selects date range: Aug 01 — Aug 05
2. [GET] /api/v1/attendance/... (existing endpoint to fetch attendance for that range)
3. Employee sees:
   - Aug 01: ABSENT
   - Aug 02: LATE
   - Aug 03: MISSED_CHECKOUT
   - Aug 04: PRESENT (not eligible — already present)
   - Aug 05: HOLIDAY (not eligible — holiday)
4. Employee selects Aug 01, 02, 03 and sets requestedStatus = PRESENT
5. Employee types reason: "Biometric device was not working"
6. [POST] /api/v1/attendance-regularizations
   → Returns RegularizationResponse with status = PENDING
```

### Manager Reviews and Approves

```
1. Manager opens Pending Requests screen
2. [GET] /api/v1/attendance-regularizations/pending
3. Manager clicks on John Doe's request
4. [GET] /api/v1/attendance-regularizations/1
5. Manager sees 3 pending details
6. Manager approves Aug 01:
   [PATCH] /api/v1/attendance-regularizations/1/details/1
   Body: { "status": "APPROVED", "approvedStatus": "PRESENT", "remarks": "Verified" }
   → Returns updated response: parent status = PARTIALLY_APPROVED
7. Manager approves Aug 02:
   [PATCH] /api/v1/attendance-regularizations/1/details/2
   → Returns updated response: parent status = PARTIALLY_APPROVED
8. Manager approves Aug 03:
   [PATCH] /api/v1/attendance-regularizations/1/details/3
   → Returns updated response: parent status = APPROVED
```

### HR Admin Reverts an Approval

```
1. HR Admin opens All Requests screen
2. [GET] /api/v1/attendance-regularizations/all
3. HR Admin finds the approved request and opens it
4. HR Admin clicks "Revert" on Aug 01 detail
5. [POST] /api/v1/attendance-regularizations/1/details/1/revert
   → Attendance for Aug 01 is restored to ABSENT
   → Detail status changes to REVERTED
   → Parent status recalculates to PARTIALLY_APPROVED
```
