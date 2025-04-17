// // Show Title Of Service When Click Button
// let bookButtons = document.querySelectorAll(".book-room-btn")
// let bookingModalLable = document.querySelector("#bookingModalLable")

// bookButtons.forEach(button => {
//     button.addEventListener("click", () =>{
//         let serviceTitle = button.getAttribute("data-title")
//         bookingModalLable.innerText = serviceTitle;
//     })
// })

function logoutUser() {
    fetch('/api/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then((response) => {
            if (response.ok) {
                // Chuyển hướng về trang login sau khi logout thành công
                window.location.href = '/login?logout';
            } else {
                alert('Logout failed. Please try again.');
            }
        })
        .catch((error) => {
            console.error('Error during logout:', error);
            alert('An error occurred. Please try again.');
        });
}

document.addEventListener("DOMContentLoaded", function () {
    const exchangeRate = 25000; // Tỷ giá USD -> VND

    const usdElements = document.querySelectorAll(".service-price-usd");
    const vndElements = document.querySelectorAll(".vnd-display");

    usdElements.forEach((usdEl, index) => {
        const usdText = usdEl.textContent.trim();
        const usdValue = parseFloat(usdText);

        if (!isNaN(usdValue) && vndElements[index]) {
            const vnd = (usdValue * exchangeRate).toLocaleString("vi-VN");
            vndElements[index].textContent = `${vnd} VND`;
        }
    });
});

