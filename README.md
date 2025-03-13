# 🌿 SkinCare Booking

## 📌 Tổng quan

SkinCare Booking là một ứng dụng web được phát triển nhằm tối ưu hóa quy trình đặt lịch chăm sóc da. Ứng dụng sử dụng **Spring Boot** làm nền tảng backend và áp dụng **kết xuất phía máy chủ (Server-Side Rendering)** cho frontend, đảm bảo hiệu suất cao và trải nghiệm mượt mà. Hệ thống được triển khai trong **Docker** và sử dụng **MySQL** để quản lý cơ sở dữ liệu một cách hiệu quả.

## 🎭 Các vai trò trong hệ thống

Hệ thống hỗ trợ 5 vai trò chính:

- 👨‍💼 **Manager**: Quản lý chung hệ thống, nhân sự, thành viên và dịch vụ.
- 👩‍🔧 **Staff**: Nhân viên hỗ trợ các quy trình vận hành và chăm sóc khách hàng.
- 👤 **Member**: Người dùng đã đăng ký, có thể đặt lịch và quản lý tài khoản cá nhân.
- 💆 **Skin Therapist**: Chuyên gia chăm sóc da, cung cấp dịch vụ cho khách hàng.
- 🧑‍💻 **Guest**: Người dùng chưa đăng ký, chỉ có thể xem dịch vụ.

## ✨ Tính năng nổi bật

- 🔐 Xác thực và phân quyền người dùng
- 📅 Hệ thống quản lý đặt lịch
- 🏥 Quản lý dịch vụ và lịch làm việc của nhân viên
- 💳 Tích hợp thanh toán
- 📊 Bảng điều khiển dành cho quản trị viên

## 🛠 Công nghệ sử dụng

- **Backend**: 🖥️ Spring Boot (Java)
- **Frontend**: 🌐 Server-Side Rendering (Thymeleaf / Khác)
- **Cơ sở dữ liệu**: 🗄️ MySQL
- **Triển khai**: 🐳 Docker
- **Quản lý dịch vụ**: 🚀 Docker Compose

## 🚀 Hướng dẫn cài đặt

### ✅ Yêu cầu hệ thống

Đảm bảo rằng hệ thống của bạn đã cài đặt:

- 🐳 Docker & Docker Compose
- ☕ Java 21+
- 🔧 Maven

### 📌 Các bước cài đặt

1. Sao chép kho lưu trữ:
   ```bash
   git clone https://github.com/QUANG221222/SkinCare-Booking-Web.git
   cd SkinCare-Booking-Web
   ```
2. Xây dựng dự án:
   ```bash
   mvn clean install
   ```
3. Khởi động ứng dụng bằng Docker:
   ```bash
   docker-compose up -d
   ```
4. Truy cập ứng dụng tại `http://localhost:8080`

## 🔌 API Endpoints

| ⚡ Phương thức | 🌍 Endpoint      | 📄 Chức năng                      |
| -------------- | ---------------- | --------------------------------- | ---------------- |
| GET            | `/services`      | Lấy danh sách dịch vụ chăm sóc da | (In Progress...) |
| POST           | `/bookings`      | Tạo đặt lịch mới                  | (In Progress...) |
| GET            | `/bookings/{id}` | Lấy thông tin chi tiết đặt lịch   | (In Progress...) |
| DELETE         | `/bookings/{id}` | Hủy đặt lịch                      | (In Progress...) |

## 🤝 Đóng góp

Chúng tôi hoan nghênh mọi đóng góp! Vui lòng làm theo các bước sau:

1. 🍴 Fork kho lưu trữ
2. 🌿 Tạo một nhánh tính năng (`git checkout -b feature-name`)
3. 💾 Commit thay đổi (`git commit -m 'Thêm tính năng mới'`)
4. 📤 Đẩy lên nhánh (`git push origin feature-name`)
5. 🔄 Tạo yêu cầu hợp nhất (Pull Request)

## 📬 Liên hệ

Nếu có bất kỳ câu hỏi hoặc đề xuất nào, vui lòng liên hệ:

- 📧 Email: nguyennhatquang.2509@gmail.com
- 🐞 GitHub Issues: [Mở yêu cầu](https://github.com/QUANG221222/skincare-booking/issues)
