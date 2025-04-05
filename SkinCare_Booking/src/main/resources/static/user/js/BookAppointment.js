document
  .getElementById("appointmentForm")
  .addEventListener("submit", function (event) {
    event.preventDefault(); // Ngăn chặn reload trang

    // Lấy dữ liệu từ form
    const skinTherapistId = document.getElementById("skinTherapist").value;
    const spaServiceId = document.getElementById("spaService").value;
    const appointmentTime = document.getElementById("datetime-local").value;

    // Kiểm tra dữ liệu đầu vào
    if (!skinTherapistId || !spaServiceId || !appointmentTime) {
      alert("bVui lòng điền đầy đủ thông tin!");
      return;
    }

    const customerId = "832ee760-0ea2-11f0-b5be-0242ac110002";
    const managerId = "f501a5db-0c7b-11f0-845a-0242ac110002";
    const status = "PENDING";
    const appointmentData = {
      customerId: customerId,
      skinTherapistId: skinTherapistId,
      spaServiceId: spaServiceId,
      managerId: managerId,
      status: status,
      appointmentTime: appointmentTime, // Đảm bảo trùng với backend
    };

    // Gửi yêu cầu POST đến API backend
    fetch("http://localhost:8080/api/appointments", {
      // Đổi URL thành endpoint BE của bạn
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(appointmentData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Đặt lịch thất bại!");
        }
        return response.json();
      })
      .then((data) => {
        alert("Đặt lịch thành công!");
        console.log(data);
        // Hiển thị thông báo thành công
        const toast = document.getElementById("successToast");
        toast.style.display = "block"; // Hiển thị toast
        const bootstrapToast = new bootstrap.Toast(toast);
        bootstrapToast.show();

        // Đóng modal sau khi đặt lịch thành công
        const bookingModal = document.getElementById("bookingModal");
        const modalInstance = bootstrap.Modal.getInstance(bookingModal);
        modalInstance.hide();
    })
    .catch((error) => {
        console.error("Lỗi:", error);
        alert("Đã xảy ra lỗi khi đặt lịch. Vui lòng thử lại!");
    });
  });
