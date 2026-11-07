const campoBusca = document.getElementById("busca");

const cards = document.querySelectorAll(".estudo-card");

const mensagemSemResultados = document.getElementById("sem-resultados");

campoBusca.addEventListener("input", function () {

    const termo = normalizarTexto(campoBusca.value);

    let encontrados = 0;

    cards.forEach(function (card) {

        const conteudo = normalizarTexto(card.textContent);

        const corresponde = conteudo.includes(termo);

        card.hidden = !corresponde;

        if (corresponde) {
            encontrados++;
        }

    });

    mensagemSemResultados.hidden = encontrados > 0;

});

function normalizarTexto(texto) {

    return texto
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");

}
