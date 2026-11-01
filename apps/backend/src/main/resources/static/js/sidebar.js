const sideBar = document.querySelector(".sidebar");
const toggleButton = document.querySelector(".sidebar-toggle");

toggleButton.addEventListener("click", function () {
  sideBar.classList.toggle("recolhida");
});
