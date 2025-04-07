// Show Title Of Service When Click Button
let bookButtons = document.querySelectorAll(".book-room-btn")
let bookingModalLable = document.querySelector("#bookingModalLable")

bookButtons.forEach(button => {
    button.addEventListener("click", () =>{
        let serviceTitle = button.getAttribute("data-title")
        bookingModalLable.innerText = serviceTitle;
    })
})

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