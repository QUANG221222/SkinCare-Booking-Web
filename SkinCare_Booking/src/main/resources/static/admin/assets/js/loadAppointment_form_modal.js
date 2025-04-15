function loadAppointmentForm(serviceId) {
  // Gửi yêu cầu đến controller với serviceId làm tham số
  $("#appointmentModalBody").load("/protected/customer/appointment?serviceId=" + serviceId, function(response, status, xhr){
    if (status === "error") {
      $("#appointmentModalBody").html("Error loading form: " + xhr.status + " " + xhr.statusText);
    } else {
      // Khi nội dung load thành công, hiển thị modal (nếu bạn dùng modal)
      $("#appointmentModal").modal("show");
    }
  });
}

// Dùng delegated event binding cho form do được load động
$(document).on("submit", "#appointmentForm", function(e) {
  e.preventDefault(); // Ngăn form submit reload trang

  let formData = $(this).serialize();
  $.ajax({
    url: "/protected/customer/appointment", // URL POST của controller
    method: "POST",
    data: formData,
    success: function(resp) {
      if (resp.status === "success") {
        showToast("Thành công", resp.message);
      } else {
        showToast("Lỗi", resp.message);
      }
    },
    error: function(xhr) {
      let errorMsg = (xhr.responseJSON && xhr.responseJSON.message) ? xhr.responseJSON.message : "Có lỗi xảy ra!";
      showToast("Lỗi", errorMsg);
    }
  });
});

// Hàm hiển thị Toast sử dụng Bootstrap
function showToast(title, message) {
  // Gán tiêu đề và nội dung cho toast
  $("#toastTitle").text(title);
  $("#toastBody").text(message);

  // Khởi tạo Bootstrap Toast (đảm bảo rằng phần tử có id "liveToast" đã được load trong DOM)
  let toastEl = document.getElementById("liveToast");
  let toast = new bootstrap.Toast(toastEl, {
    delay: 3000   // thời gian hiển thị toast (ms)
  });
  toast.show();
}
