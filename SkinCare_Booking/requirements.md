git # Java Spring Boot Web Application

## Context

- Phần mềm đặt dịch vụ chăm sóc da dành cho các trung tâm chăm sóc da chuyên nghiệp.
- Hệ thống cho phép khách hàng tìm hiểu thông tin về các dịch vụ, chuyên viên và đặt lịch hẹn.
- Cung cấp các công cụ để quản lý lịch hẹn, thông tin khách hàng, đánh giá dịch vụ và báo cáo hoạt động.
- Tích hợp các tính năng hỗ trợ như: trắc nghiệm da, đề xuất dịch vụ phù hợp.
- Giúp trung tâm nâng cao hiệu quả quản lý và chất lượng dịch vụ.

## Actors ✅

- **Guest (Khách vãng lai):** Khách hàng tiềm năng, chưa có tài khoản, tìm hiểu thông tin.
- **Customer (Khách hàng):** Người dùng đã đăng ký, có tài khoản, đặt lịch hẹn, quản lý thông tin cá nhân và đánh giá dịch vụ.
- **Skin Therapist (Chuyên viên trị liệu da):** Thực hiện dịch vụ chăm sóc da, quản lý lịch làm việc, xem thông tin khách hàng và ghi nhận kết quả điều trị.
- **Staff (Nhân viên):** Hỗ trợ khách hàng đặt lịch, quản lý thông tin trung tâm, quản lý các hoạt động hành chính khác.
- **Manager (Quản lý):** Quản lý hoạt động của trung tâm, theo dõi doanh thu, đánh giá hiệu quả dịch vụ, xem báo cáo và phân tích.

## System Requirements

### 1 Frontend Requirements

#### 1.1 Guest and Customer

- **Trang chủ (Guest/Public)**
  - Giới thiệu ✅
  - Hiển thị thông tin các dịch vụ chăm sóc da ✅
  - Hiển thị thông tin chuyên viên trị liệu da ✅
  - Blog giới thiệu ✅
  - Tin tức ✅
- **Trang đặt lịch (Customer)**
  - Cho phép khách hàng đặt lịch chăm sóc da
  - Chỉ định chuyên viên
- **Trang quản lý lịch hẹn (Customer)**
  - Xem lại các lịch hẹn đã đặt 
  - Thay đổi thông tin lịch hẹn, thay đổi chuyên viên 
  - Huỷ lịch hẹn 
  - Lựa chọn phương thức thanh toán (trả tiền mặt sau khi hoàn tất dịch vụ, thanh toán trước qua chuyển khoảng...) 
- **Trang đánh giá (Customer)**
  - Cho phép khách hàng viết nhận xét và đánh giá dịch vụ ✅
  - Cho phép khách hàng xoá nhận xét, đánh giá ✅
- **Trang hồ sơ (Customer)**
  - Cho phép xem thông tin cá nhân đã đăng ký với trung tâm ✅
  - Cho phép thay đổi một số thông tin cá nhân ✅
  - Cho phép thay đổi mật khẩu ✅

#### 1.2 Skin Therapist

- **Trang hồ sơ (Skin Therapist)**
  - Cho phép xem thông tin cá nhân đã đăng ký với trung tâm, vai trò, chuyên môn, lịch làm việc
  - Cho phép thay đổi một số thông tin cá nhân
  - Cho phép thay đổi mật khẩu
- **Trang quản lý dịch vụ/lịch hẹn**
  - Cho phép xem các lịch hẹn khách hàng đã đặt với bản thân
  - Cho phép lưu lại kết quả thực hiện dịch vụ
  - Cho phép lưu lại một số lưu ý khác về dịch vụ đã thực hiện

#### 1.3 Staff

- **Trang hồ sơ (Staff)**
  - Cho phép xem thông tin cá nhân đã đăng ký với trung tâm, vai trò, lịch làm việc
  - Cho phép thay đổi một số thông tin cá nhân
  - Cho phép thay đổi mật khẩu
- **Trang quản lý dịch vụ/lịch hẹn**
  - Cho phép xem tất cả các lịch hẹn đã được khách hàng đặt
  - Hiển thị thông tin chi tiết về lịch hẹn
  - Cập nhật trạng thái lịch hẹn (Đã đặt/Chưa thanh toán/Đã thanh toán/Đã hoàn tất)
- **Trang thông tin khách hàng**
  - Cho phép xem thông tin cá nhân của khách hàng
  - Xem lịch sử đặt dịch vụ của khách hàng

#### 1.4 Manager

- **Trang hồ sơ (Manager)**
  - Cho phép xem thông tin cá nhân đã đăng ký với trung tâm, vai trò, lịch làm việc ✅
  - Cho phép thay đổi một số thông tin cá nhân ✅
  - Cho phép thay đổi mật khẩu ✅
- **Trang quản lý nhân sự**
  - Xem thông tin chi tiết các nhân viên, chuyên viên dưới sự quản lí của bản thân ✅
  - Thêm nhân sự mới vào danh sách quản lý ✅
  - Gỡ nhân sự ra khỏi danh sách quản lý
- **Trang quản lý lịch hẹn**
  - Xem tất cả các lịch hẹn dịch vụ 
  - Xem các lịch hẹn mà bản thân quản lý
  - Hiển thị thông tin chi tiết về lịch hẹn
  - Cập nhật một số thông tin trong các lịch hẹn mà bản thân quản lý
  - Cập nhật trạng thái lịch hẹn (Đã đặt/Chưa thanh toán/Đã thanh toán/Đã hoàn tất)
- **Trang quản lí feedback**
  - Xem các rating, feedback của khách hàng
- **Trang dashboard, report**
  - Hiển thị báo cáo doanh thu (theo tuần/theo tháng)
  - Hiển thị báo cáo về lịch hẹn. Ví dụ:
    - Tổng số lịch hẹn (theo tuần/theo tháng)
    - Số lịch hẹn đã hoàn thành
    - Số lịch hẹn đã huỷ

### 2 Backend Requirements

#### 2.1 Authentication & Authorization

##### Authentication

- **JWT Authentication:**

  - Tạo và quản lý JWT (JSON Web Tokens) cho người dùng sau khi đăng nhập thành công. ✅
  - JWT sẽ được gửi trong header `Authorization` (Bearer Token) cho các yêu cầu được bảo vệ. ✅
  - **Endpoints liên quan:**
    - `/auth/login` (POST): Xử lý đăng nhập, xác thực thông tin đăng nhập và trả về JWT. ✅
    - `/auth/register` (POST): Xử lý đăng ký tài khoản mới (nếu có). ✅
    - `/auth/refresh-token` (POST): (Optional) API làm mới token (khi token hết hạn). ✅

- **Mã Hóa Mật Khẩu:**

  - Sử dụng `PasswordEncoder` (ví dụ: `BCryptPasswordEncoder`) của Spring Security. ✅
  - Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu. ✅
  - So sánh mật khẩu đã nhập với mật khẩu đã mã hóa trong quá trình xác thực đăng nhập. ✅

- **Xác Thực Email (Email Verification):**

  - Tạo và gửi email xác thực khi người dùng đăng ký. ✅
  - Lưu trạng thái đã xác thực email của người dùng trong cơ sở dữ liệu (ví dụ: `isVerified`). ✅
  - Cung cấp endpoint để người dùng xác thực email (thông qua link trong email). ✅
  - **Endpoints liên quan:**
    - `/auth/register` (POST): Đăng ký, gửi email xác thực. ✅
    - `/auth/verify-email` (GET): Xác thực email (sử dụng token hoặc code được gửi trong email). ✅

- **Thay Đổi Mật Khẩu:**
  - Cung cấp endpoint cho phép người dùng thay đổi mật khẩu. ✅
  - Yêu cầu xác thực người dùng (dùng JWT). ✅
  - Xác nhận mật khẩu cũ (tùy chọn). ✅
  - Lưu mật khẩu mới đã mã hóa. ✅
  - **Endpoints liên quan:**
    - `/auth/change-password` (POST/PUT): Thay đổi mật khẩu (yêu cầu authentication). ✅

#### Authorization

- **Kiểm Soát Quyền Truy Cập:**
  - Sử dụng Spring Security để phân quyền dựa trên vai trò (role) của người dùng. ✅
  - Gán các vai trò (e.g., `ROLE_GUEST`, `ROLE_CUSTOMER`, `ROLE_SKIN_THERAPIST`, `ROLE_STAFF`, `ROLE_MANAGER`) cho người dùng. ✅
  - Sử dụng `@PreAuthorize`, `@RolesAllowed`, hoặc các phương thức khác của Spring Security để hạn chế quyền truy cập vào các API. ✅
  - **Ví dụ:**
    - `@PreAuthorize("hasRole('ROLE_CUSTOMER')")`: Chỉ cho phép người dùng có vai trò "CUSTOMER" truy cập.
    - `@PreAuthorize("hasAnyRole('ROLE_STAFF', 'ROLE_MANAGER')")`: Cho phép người dùng có vai trò "STAFF" hoặc "MANAGER" truy cập.

#### 2.2 Models

**User (Người dùng)** ✅

- `id` (Long/UUID): ID người dùng (primary key).
- `username` (String): Tên đăng nhập (unique).
- `password` (String): Mật khẩu (đã mã hóa).
- `email` (String): Địa chỉ email (unique).
- `firstName` (String): Tên.
- `lastName` (String): Họ.
- `phoneNumber` (String): Số điện thoại.
- `address` (String): Địa chỉ.
- `role` (String): Vai trò của người dùng (e.g., `ROLE_CUSTOMER`, `ROLE_SKIN_THERAPIST`, ...).
- `isVerified` (Boolean): Trạng thái email đã được xác thực (default: `false`).
- `createdAt` (Date/Timestamp): Ngày tạo.
- `updatedAt` (Date/Timestamp): Ngày cập nhật.
  - **Table name**: `users`

**Customer (Khách hàng)** ✅

- `userId` (Long): ID người dùng (foreign key tham chiếu đến bảng `users`).
- Các thông tin bổ sung liên quan đến khách hàng (nếu cần).
  - **Table name**: `customers`

**SkinTherapist (Chuyên viên chăm sóc da)**✅

- `userId` (Long): ID người dùng (foreign key tham chiếu đến bảng `users`).
- `specialization` (String): Chuyên môn (ví dụ: trị mụn, trẻ hóa da,...).
- `workingSchedule` (String): Lịch làm việc (có thể lưu dưới dạng JSON).
  - **Table name**: `skin_therapists`

**Staff (Nhân viên)**✅

- `userId` (Long): ID người dùng (foreign key tham chiếu đến bảng `users`).
- Các thông tin bổ sung liên quan đến nhân viên.
  - **Table name**: `staffs`

**Service (Dịch vụ)**✅

- `id` (Long/UUID): ID dịch vụ (primary key).
- `name` (String): Tên dịch vụ.
- `description` (String): Mô tả dịch vụ.
- `price` (Double): Giá dịch vụ.
- `duration` (Integer): Thời gian thực hiện (phút).
- `image` (String): Đường dẫn đến hình ảnh dịch vụ (tùy chọn).
- `isActive` (Boolean): Trạng thái hoạt động (default: `true`).
- `createdAt` (Date/Timestamp).
- `updatedAt` (Date/Timestamp).
  - **Table name**: `services`

**Appointment (Lịch hẹn)**✅

- `id` (Long/UUID): ID lịch hẹn (primary key).
- `customerId` (Long): ID khách hàng (foreign key tham chiếu đến bảng `customers`).
- `serviceId` (Long): ID dịch vụ (foreign key tham chiếu đến bảng `services`).
- `skinTherapistId` (Long): ID chuyên viên (foreign key tham chiếu đến bảng `skin_therapists`).
- `appointmentTime` (LocalDateTime): Thời gian đặt lịch.
- `status` (String): Trạng thái lịch hẹn (e.g., `PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`, `PAID`, `UNPAID`).
- `paymentMethod` (String): Phương thức thanh toán (e.g., `CASH`, `BANK_TRANSFER`).
- `notes` (String): Ghi chú của khách hàng.
- `skinTherapistNotes` (String): Ghi chú của chuyên viên.
- `createdAt` (Date/Timestamp).
- `updatedAt` (Date/Timestamp).
  - **Table name**: `appointments`

**Feedback (Đánh giá)**✅

- `id` (Long/UUID): ID feedback (primary key).
- `customerId` (Long): ID khách hàng (foreign key tham chiếu đến bảng `customers`).
- `appointmentId` (Long): ID lịch hẹn (foreign key tham chiếu đến bảng `appointments`).
- `rating` (Integer): Đánh giá (1-5 sao).
- `comment` (String): Nhận xét.
- `createdAt` (Date/Timestamp).
- `updatedAt` (Date/Timestamp).
  - **Table name**: `feedbacks`

**Blog/News (Tin tức)** (Optional - nếu cần thiết)✅

- `id` (Long/UUID): ID bài viết (primary key).
- `title` (String): Tiêu đề.
- `content` (String): Nội dung.
- `author` (String): Tác giả.
- `createdAt` (Date/Timestamp).
- `updatedAt` (Date/Timestamp).
  - **Table name**: `blogs`

#### 2.4 Services

**UserService**

- Xử lý các thao tác liên quan đến người dùng (User).
- Đăng ký, đăng nhập, thay đổi mật khẩu, xác thực email.
- Quản lý thông tin cá nhân của người dùng.
- **Methods:**
  - `register(UserDTO userDTO)`: Đăng ký người dùng mới.
  - `login(String username, String password)`: Đăng nhập và trả về JWT.
  - `changePassword(String username, String oldPassword, String newPassword)`: Thay đổi mật khẩu.
  - `verifyEmail(String token)`: Xác thực email.
  - `getUserProfile(Long userId)`: Lấy thông tin cá nhân của người dùng.
  - `updateUserProfile(Long userId, UserDTO userDTO)`: Cập nhật thông tin cá nhân.

**CustomerService**

- Xử lý các thao tác liên quan đến khách hàng.
- **Methods:**
  - `getCustomerProfile(Long customerId)`: Lấy thông tin khách hàng.
  - `updateCustomerProfile(Long customerId, CustomerDTO customerDTO)`: Cập nhật thông tin khách hàng.

**SkinTherapistService**

- Xử lý các thao tác liên quan đến chuyên viên.
- **Methods:**
  - `getSkinTherapistProfile(Long skinTherapistId)`: Lấy thông tin chuyên viên.
  - `updateSkinTherapistProfile(Long skinTherapistId, SkinTherapistDTO skinTherapistDTO)`: Cập nhật thông tin chuyên viên.

**StaffService**

- Xử lý các thao tác liên quan đến nhân viên.
- **Methods:**
  - `getStaffProfile(Long staffId)`: Lấy thông tin nhân viên.
  - `updateStaffProfile(Long staffId, StaffDTO staffDTO)`: Cập nhật thông tin nhân viên.

**ManagerService**

- Xử lý các thao tác liên quan đến quản lý.
- **Methods:**
  - `getAllStaff()`: Lấy danh sách tất cả nhân viên.
  - `addStaff(Long userId)`: Thêm nhân viên.
  - `removeStaff(Long userId)`: Xóa nhân viên.

**ServiceService**

- Quản lý thông tin dịch vụ.
- **Methods:**
  - `getAllServices()`: Lấy danh sách dịch vụ.
  - `getServiceById(Long serviceId)`: Lấy thông tin dịch vụ theo ID.
  - `createService(ServiceDTO serviceDTO)`: Tạo dịch vụ mới (quản lý).
  - `updateService(Long serviceId, ServiceDTO serviceDTO)`: Cập nhật dịch vụ (quản lý).
  - `deleteService(Long serviceId)`: Xóa dịch vụ (quản lý).

**AppointmentService**

- Quản lý lịch hẹn.
- **Methods:**
  - `createAppointment(AppointmentDTO appointmentDTO)`: Đặt lịch hẹn.
  - `getAppointmentById(Long appointmentId)`: Lấy thông tin lịch hẹn.
  - `getAppointmentsByCustomerId(Long customerId)`: Lấy danh sách lịch hẹn của khách hàng.
  - `getAppointmentsBySkinTherapistId(Long skinTherapistId)`: Lấy danh sách lịch hẹn của chuyên viên.
  - `getAllAppointments()`: Lấy danh sách tất cả lịch hẹn (quản lý, staff).
  - `updateAppointmentStatus(Long appointmentId, String status)`: Cập nhật trạng thái lịch hẹn.
  - `updateAppointment(Long appointmentId, AppointmentDTO appointmentDTO)`: Cập nhật thông tin lịch hẹn (khách hàng, staff, quản lý).
  - `cancelAppointment(Long appointmentId, Long customerId)`: Hủy lịch hẹn (khách hàng).

**FeedbackService**

- Quản lý đánh giá, feedback.
- **Methods:**
  - `createFeedback(FeedbackDTO feedbackDTO)`: Tạo feedback.
  - `getFeedbackByAppointmentId(Long appointmentId)`: Lấy feedback theo lịch hẹn.
  - `getFeedbacksByCustomerId(Long customerId)`: Lấy danh sách feedback của khách hàng.
  - `deleteFeedback(Long feedbackId, Long customerId)`: Xóa feedback.

**ReportService** (Optional)

- **Methods:**
  - `generateSalesReport(Date startDate, Date endDate)`: Báo cáo doanh thu.
  - `generateAppointmentReport(Date startDate, Date endDate)`: Báo cáo lịch hẹn.

#### 2.5 Integration

**Database:**

- Sử dụng một hệ quản trị cơ sở dữ liệu (DBMS). Trong trường hợp này là MySQL
- Sử dụng Spring Data JPA để tương tác với cơ sở dữ liệu.
- Thiết kế các bảng và quan hệ như đã nêu trong phần "Models".

**Email Service:**

- Sử dụng thư viện Spring Boot Mail (hoặc các thư viện khác) để gửi email.
- Cấu hình thông tin SMTP (Simple Mail Transfer Protocol) của nhà cung cấp email (ví dụ: Gmail, SendGrid).
- Gửi email xác thực, thông báo đặt lịch, thông báo thanh toán,...

**Payment Gateway:**

- Trong trường hợp này có thể đơn giản hoá bằng cách cho phép khách hàng lựa chọn
  - Thanh toán tại quầy sau khi sử dụng dịch vụ
  - Thanh toán trước qua chuyển khoản, gửi email/hiển thị trên trang web số tiền cần thanh toán và thông tin chuyển khoản (có thể tạo mã QR chuyển khoảng)

**Logging:**

- Sử dụng một framework logging (ví dụ: Logback, Log4j2) để ghi lại các sự kiện quan trọng trong ứng dụng.
- Ghi log thông tin đăng nhập, lỗi, các thay đổi dữ liệu,...
- **<u>Note</u>:** Có thể đơn giản hoá bằng cách ghi trực tiếp vào database và chia thành 2 phân loại log (`info` và `error`)

**RESTful API:**

- Thiết kế các API RESTful cho các chức năng được yêu cầu bởi frontend.
- Sử dụng Spring MVC để tạo các controller và endpoints.
- Sử dụng DTO (Data Transfer Objects) để trao đổi dữ liệu giữa backend và frontend.
- Sử dụng `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` để định nghĩa các API.
- Sử dụng `@RequestBody`, `@PathVariable`, `@RequestParam` để xử lý các tham số trong request.
- Xử lý và trả về các status code HTTP phù hợp (e.g., 200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 500 Internal Server Error).
- Sử dụng JSON (JavaScript Object Notation) làm định dạng dữ liệu chính cho API.
