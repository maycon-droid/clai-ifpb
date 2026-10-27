const campoBusca = document.getElementById("busca");
const campoAlunos = document.querySelector(".alunos-lista");

campoBusca.addEventListener("input", function () {
  const termo = campoBusca.value.toLowerCase();

  const linhas = campoAlunos.querySelectorAll("li");

  linhas.forEach(function (linha) {
    const nome = linha.querySelector("h4").textContent.toLowerCase();
    const matricula = linha
      .querySelector(".aluno-info p span")
      .textContent.toLowerCase();

    const encontrou = nome.includes(termo) || matricula.includes(termo);

    linha.style.display = encontrou ? "" : "none";
  });
});

const sideBar = document.querySelector(".sidebar");
const toggleButton = document.querySelector(".sidebar-toggle");

toggleButton.addEventListener("click", function () {
  sideBar.classList.toggle("recolhida");
});
