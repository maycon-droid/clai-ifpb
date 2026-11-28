function iniciarSidebar() {
    const sideBar = document.querySelector(".sidebar");

    if (!sideBar || sideBar.dataset.inicializada === "true") return;

    sideBar.dataset.inicializada = "true";

    const toggleButton = sideBar.querySelector(".sidebar-toggle");
    const documentosButton = sideBar.querySelector(".documentos-toggle");
    const documentosMenu = sideBar.querySelector(".sidebar-submenu");

    // Recolher e expandir a sidebar
    if (toggleButton) {
        toggleButton.addEventListener("click", function () {
            const recolhida = sideBar.classList.toggle("recolhida");

            toggleButton.setAttribute("aria-expanded", String(!recolhida));

            if (recolhida && documentosMenu && documentosButton) {
                documentosMenu.classList.add("fechado");
                documentosButton.setAttribute("aria-expanded", "false");
            }
        });
    }

    // Abrir e fechar Documentos
    if (documentosButton && documentosMenu) {
        documentosButton.addEventListener("click", function () {
            if (sideBar.classList.contains("recolhida")) return;

            const aberto = documentosButton.getAttribute("aria-expanded") === "true";

            documentosButton.setAttribute("aria-expanded", String(!aberto));
            documentosMenu.classList.toggle("fechado", aberto);
        });
    }

    // Identificar e destacar a página atual
    const caminhoAtual = window.location.pathname.replace(/\/+$/, "") || "/";
    const links = sideBar.querySelectorAll("a.sidebar-link:not(.em-desenvolvimento)");

    links.forEach(function (link) {
        const url = new URL(link.href, window.location.origin);
        const caminhoLink = url.pathname.replace(/\/+$/, "") || "/";

        if (caminhoLink === caminhoAtual) {
            link.classList.add("ativo");
            link.setAttribute("aria-current", "page");

            if (documentosMenu && documentosMenu.contains(link)) {
                documentosMenu.classList.remove("fechado");

                if (documentosButton) {
                    documentosButton.setAttribute("aria-expanded", "true");
                }
            }
        }
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", iniciarSidebar);
} else {
    iniciarSidebar();
}
