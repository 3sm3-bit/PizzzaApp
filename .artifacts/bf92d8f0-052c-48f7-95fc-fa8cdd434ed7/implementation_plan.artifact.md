# Implementation Plan - Fix Branch Update Error

The user is experiencing an "Unexpected Error" when updating a branch. Analysis reveals that the networking layer is catching a 4xx/5xx error from the server but failing to provide a descriptive message because it fails to parse the error body. Additionally, the `PUT` request URL for updating a branch is missing the required unique identifier (`uid`) in the path, which is a common REST requirement and likely causing a `404 Not Found` or `405 Method Not Allowed`.

## Proposed Changes

### Shared Module

#### [MODIFY] [NetworkModule.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/src/commonMain/kotlin/com/tayler/pizzzaapp/repository/di/NetworkModule.kt)
- Update `HttpResponseValidator` to use `isSuccess()` instead of a strict `HttpStatusCode.OK` check. This prevents success codes like `201 Created` or `204 No Content` from being treated as errors.
- Improve error parsing:
    - Use a `Json` instance with `ignoreUnknownKeys = true` to parse `CompleteErrorModel`.
    - Fallback to the raw `errorText` if JSON parsing fails, ensuring the user (or logs) can see the actual server error.
    - Include the HTTP status code in the error title for easier debugging.

#### [MODIFY] [KmmService.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/src/commonMain/kotlin/com/tayler/pizzzaapp/repository/network/KmmService.kt)
- Update `updateBranch`, `updateProduct`, and `updateParentOrder` endpoints to include the resource ID in the URL path (e.g., `${BASE_URL}/branch/${request.uid}`). This aligns with standard RESTful API design for `PUT` operations.

#### [MODIFY] [extensions.kt](file:///Users/tayler/Desktop/project/android/PizzzaApp/shared/src/commonMain/kotlin/com/tayler/pizzzaapp/repository/utils/extensions.kt)
- Add a version of `parseJsonTo` that accepts a `Json` instance, or update the default one to be more lenient.

## Verification Plan

### Manual Verification
- Deploy the app to an Android device/emulator.
- Navigate to the "Sucursales" section.
- Select a branch and edit its details.
- Tap "Guardar Cambios".
- Verify that the branch is updated successfully without the "Unexpected Error".
- If it still fails, the new error handling in `NetworkModule.kt` should provide a more specific message (e.g., the actual HTTP status code or server response).
