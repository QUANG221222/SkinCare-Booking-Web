// Show Title Of Service When Click Button
let bookButtons = document.querySelectorAll(".book-room-btn")
let bookingModalLable = document.querySelector("#bookingModalLable")

console.log(bookButtons)
bookButtons.forEach(button => {
    button.addEventListener("click", () =>{
        let serviceTitle = button.getAttribute("data-title")
        bookingModalLable.innerText = serviceTitle;
    })
})