# FEAT-ZAFRAN-FEATURES-BE-INTEGRATION.md
# Role: Senior Frontend Architect
# Context: Integration for Modul 1 (Zafran Features) - Phase 1

This document outlines the required Frontend changes to integrate with the updated Backend logic for User Profiles and the Jastiper Upgrade Flow.

## 1. API Contract Updates

### 1.1 Authentication (Login)
- **Endpoint**: `POST /api/login`
- **Response Change**: The `id` field in `UserLoginResponse` is now a **UUID** (String format).
- **Error Handling**: 
    - Failed logins now return a generic `401 Unauthorized` with a structured `ProblemDetail` JSON.
    - **Brute Force Defense**: If a user is locked out (5 failed attempts), the API returns:
      ```json
      {
        "title": "AUTHENTICATION_FAILED",
        "detail": "Account is locked due to too many failed attempts. Try again in 5 minutes.",
        "errorCode": "AUTH_001"
      }
      ```
- **FE Task**: Update the Login view to handle generic error messages and display the specific lockout timer message if `errorCode: "AUTH_001"` is received.

### 1.2 Jastiper Upgrade Submission
- **Endpoint**: `POST /api/upgrade-request/submit`
- **Protection**: Requires `JWT` with `ROLE_TITIPER`.
- **Request DTO**:
  ```json
  {
    "fullName": "String",
    "credential": "String (URL/Base64)"
  }
  ```
- **FE Task**: Create the "Upgrade Portal" form. Ensure strict validation before submission (Full Name and Credential are required).

### 1.3 Admin Upgrade Decision
- **Endpoint**: `PATCH /api/upgrade-request/change-status/{requestId}`
- **Protection**: Requires `JWT` with `ROLE_ADMIN`.
- **Path Variable**: `{requestId}` is now a **UUID**.
- **FE Task**: Update the Admin Dashboard to send the `UUID` of the request instead of a numeric ID or username.

## 2. Security & Identity Management

### 2.1 UUID Handling
- All primary identifiers (`User.id`, `UpgradeRequest.id`) are now UUIDs. 
- **FE Task**: Update TypeScript interfaces to use `string` for these IDs but treat them as opaque tokens. Do not attempt to increment or predict them.

### 2.2 Role-Based Navigation
- After an Admin approves an upgrade, the user's role on the Backend changes to `JASTIPER`.
- **FE Task**: Implement a "Role Refresh" logic or re-login prompt when a user's role evolves to ensure the UI unlocks Jastiper-specific features (e.g., Post Product).

## 3. Observability & Debugging

- **Verbose Mode**: The Backend now supports `app.debug.verbose`.
- **FE Task**: If the FE is running in a `development` environment, ensure all API error `ProblemDetail` fields (like `errorCode` and `detail`) are logged to the browser console for easier debugging with the BE team.

## 4. Definition of Done for FE
- [ ] Forms for `/api/upgrade-request/submit` include client-side validation.
- [ ] Error handling supports the `ProblemDetail` format.
- [ ] All ID references in state/props updated to support UUID strings.
- [ ] Role-based access in the UI correctly gates the Upgrade Portal (only for TITIPER).
