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
      alert("Vui lòng điền đầy đủ thông tin!");
      return;
    }

    // Tạo object dữ liệu để gửi đến backend
    const appointmentData = {
      skinTherapistId: skinTherapistId,
      spaServiceId: spaServiceId,
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
      })
      .catch((error) => {
        console.error("Lỗi:", error);
        alert("Đã xảy ra lỗi khi đặt lịch. Vui lòng thử lại!");
      });
  });
