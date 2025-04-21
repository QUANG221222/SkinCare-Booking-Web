function loadAppointmentForm(serviceId) {
  $("#appointmentModalBody").load("/protected/customer/appointment?serviceId=" + serviceId, function(response, status, xhr) {
      if (status === "error") {
          $("#appointmentModalBody").html("Error loading form: " + xhr.status + " " + xhr.statusText);
      } else {
          $("#appointmentModal").modal("show");
      }
  });
}

$(document).on("submit", "#appointmentForm", function(e) {
  e.preventDefault();

  let formData = $(this).serialize();

  $.ajax({
      url: "/protected/customer/appointment/create",
      method: "POST",
      data: formData,
      dataType: "json",
      success: function(resp) {
          if (resp.status === "success") {
              showToast("Success", resp.message);
              $("#appointmentModal").modal("hide");
              window.location.reload();
          } else {
              showToast("Error", resp.message);
          }
      },
      error: function(xhr) {
          let errorMsg = "An error occurred!";
          try {
              let response = JSON.parse(xhr.responseText);
              errorMsg = response.message || errorMsg;
          } catch (e) {
              errorMsg = xhr.responseText || errorMsg;
          }
          showToast("Error", errorMsg);
      }
  });
});

function showToast(title, message) {
  $("#toastTitle").text(title);
  $("#toastBody").text(message);

  let toastEl = document.getElementById("liveToast");
  let toast = new bootstrap.Toast(toastEl, {
      delay: 3000
  });
  toast.show();
}