document.addEventListener("DOMContentLoaded", function () {
    const formulario = document.getElementById("formulario-estudo");

    if (!formulario) return;

    const alunos = window.alunosDisponiveis || [];
    const estudo = window.estudoAtual || {};

    /* Autocomplete de alunos */

    const campoBusca = document.getElementById("busca-aluno");
    const sugestoes = document.getElementById("sugestoes-alunos");
    const campoAlunoId = document.getElementById("aluno-id");
    const dadosAluno = document.getElementById("dados-aluno");
    const alunoSelecionado = document.getElementById("aluno-selecionado");

    function preencherDadosAluno(aluno) {
        if (!aluno || !alunoSelecionado || !dadosAluno) return;

        document.getElementById("aluno-nome").value = aluno.nome || "";
        document.getElementById("aluno-cpf").value = aluno.cpf || "";
        document.getElementById("aluno-matricula").value =
            aluno.matricula?.numeroMatricula || "";
        document.getElementById("aluno-curso").value =
            aluno.matricula?.curso || "";
        document.getElementById("aluno-ingresso").value =
            aluno.matricula?.periodoIngresso || "";
        document.getElementById("aluno-periodo").value =
            aluno.matricula?.periodoLetivo || "";

        alunoSelecionado.hidden = false;
        dadosAluno.classList.add("selecionado");
    }

    function selecionarAluno(aluno) {
        campoAlunoId.value = aluno.id;
        campoBusca.value = aluno.nome;

        preencherDadosAluno(aluno);

        sugestoes.innerHTML = "";
        sugestoes.hidden = true;
        sugestoes.classList.remove("ativa");
    }

    if (campoBusca && sugestoes && campoAlunoId) {
        campoBusca.addEventListener("input", function () {
            const termo = normalizarTexto(campoBusca.value);

            campoAlunoId.value = "";

            if (alunoSelecionado) {
                alunoSelecionado.hidden = true;
            }

            if (dadosAluno) {
                dadosAluno.classList.remove("selecionado");
            }

            sugestoes.innerHTML = "";

            if (termo.length === 0) {
                sugestoes.hidden = true;
        sugestoes.classList.remove("ativa");
                return;
            }

            const encontrados = alunos.filter(function (aluno) {
                const nome = normalizarTexto(aluno.nome || "");
                const matricula = normalizarTexto(
                    aluno.matricula?.numeroMatricula || ""
                );

                return nome.includes(termo) || matricula.includes(termo);
            });

            encontrados.forEach(function (aluno) {
                const item = document.createElement("div");

                item.className = "sugestao-aluno";

                const nome = document.createElement("strong");
                nome.textContent = aluno.nome || "Nome não informado";

                const matricula = document.createElement("span");
                matricula.textContent =
                    "Matrícula: " +
                    (aluno.matricula?.numeroMatricula || "Não informada");

                const curso = document.createElement("span");
                curso.textContent =
                    "Curso: " + (aluno.matricula?.curso || "Não informado");

                item.append(nome, matricula, curso);

                item.addEventListener("click", function () {
                    selecionarAluno(aluno);
                });

                sugestoes.appendChild(item);
            });

            if (encontrados.length === 0) {
                const mensagem = document.createElement("p");
                mensagem.textContent = "Nenhum aluno encontrado.";
                sugestoes.appendChild(mensagem);
            }

            sugestoes.hidden = false;
            sugestoes.classList.add("ativa");
        });

        const alunoIdInicial = campoAlunoId.value;

        if (alunoIdInicial) {
            const alunoInicial = alunos.find(
                aluno => aluno.id === alunoIdInicial
            );

            if (alunoInicial) {
                selecionarAluno(alunoInicial);
            }
        }
    }

    /* Normalização de texto */

    function normalizarTexto(texto) {
        return texto
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }

    /* Datas de atendimento */

    const listaDatas = document.getElementById("lista-datas");
    const adicionarData = document.getElementById("adicionar-data");

    function atualizarIndicesDatas() {
        listaDatas.querySelectorAll(".item-dinamico").forEach(function (item, indice) {
            const input = item.querySelector("input");

            input.name = `datasAtendimentoAnamnese[${indice}]`;
            input.id = `datasAtendimentoAnamnese${indice}`;
        });
    }

    function criarCampoData(valor = "") {
        const item = document.createElement("div");
        item.className = "item-dinamico";

        const input = document.createElement("input");
        input.type = "date";
        input.value = valor;

        const remover = document.createElement("button");
        remover.type = "button";
        remover.className = "btn-remover";
        remover.textContent = "Remover";

        item.append(input, remover);
        listaDatas.appendChild(item);

        atualizarIndicesDatas();
    }

    if (listaDatas && adicionarData) {
        adicionarData.addEventListener("click", function () {
            criarCampoData();
        });
    }

    /* Barreiras educacionais */

    const listaBarreiras = document.getElementById("lista-barreiras");
    const adicionarBarreira = document.getElementById("adicionar-barreira");

    function atualizarIndicesBarreiras() {
        listaBarreiras.querySelectorAll(".barreira-item").forEach(
            function (item, indice) {
                item.querySelectorAll("select, textarea").forEach(function (campo) {
    const propriedade =
        campo.dataset.campo || campo.name.split(".").pop();

    campo.name =
        `barreirasEducacionais[${indice}].${propriedade}`;
});
            }
        );
    }

    function criarBarreira(dados = {}) {
        const item = document.createElement("div");
        item.className = "barreira-item";

        const titulo = document.createElement("h3");
        titulo.textContent = "Barreira educacional";

        item.appendChild(titulo);

        function criarCampo(labelTexto, tipo, propriedade, opcoes = []) {
            const campo = document.createElement("div");
            campo.className = "campo";

            const label = document.createElement("label");
            label.textContent = labelTexto;

            let controle;

            if (tipo === "select") {
                controle = document.createElement("select");

                const vazio = document.createElement("option");
                vazio.value = "";
                vazio.textContent = "Selecione";
                controle.appendChild(vazio);

                opcoes.forEach(function (opcao) {
                    const option = document.createElement("option");
                    option.value = opcao.valor;
                    option.textContent = opcao.texto;
                    controle.appendChild(option);
                });
            } else {
                controle = document.createElement("textarea");
            }

            controle.dataset.campo = propriedade;
            controle.value = dados[propriedade] || "";

            campo.append(label, controle);
            item.appendChild(campo);
        }

        criarCampo("Dimensão da barreira", "select", "dimensao", [
            { valor: "ACESSO_FISICO_AMBIENTAL", texto: "Acesso físico e ambiental" },
            { valor: "COMUNICACAO", texto: "Comunicação" },
            { valor: "CURRICULO_METODOLOGIA", texto: "Currículo e metodologia" },
            { valor: "AVALIACAO", texto: "Avaliação" },
            { valor: "ORGANIZACAO_TEMPORALIDADE", texto: "Organização e temporalidade" },
            { valor: "INTERACAO_PARTICIPACAO", texto: "Interação e participação" },
            { valor: "TECNOLOGIA_ASSISTIVA_RECURSOS", texto: "Tecnologia assistiva e recursos" },
            { valor: "OUTRA", texto: "Outra" }
        ]);

        criarCampo(
            "Barreira ou necessidade identificada",
            "textarea",
            "barreiraNecessidadeIdentificada"
        );

        criarCampo(
            "Evidências e impactos na aprendizagem",
            "textarea",
            "evidenciasImpactosAprendizagem"
        );

        criarCampo("Prioridade", "select", "prioridade", [
            { valor: "ALTA", texto: "Alta" },
            { valor: "MEDIA", texto: "Média" },
            { valor: "BAIXA", texto: "Baixa" }
        ]);

        const remover = document.createElement("button");
        remover.type = "button";
        remover.className = "btn-remover";
        remover.textContent = "Remover barreira";

        item.appendChild(remover);
        listaBarreiras.appendChild(item);

        atualizarIndicesBarreiras();
    }

    if (listaBarreiras && adicionarBarreira) {
        adicionarBarreira.addEventListener("click", function () {
            criarBarreira();
        });
    }

    /* Listas de apoios e adaptações */

    const apoios = estudo.apoiosAdaptacoes || {};

    document.querySelectorAll(".lista-textos").forEach(function (lista) {
        const propriedade = lista.dataset.name;
        const valores = apoios[propriedade.split(".")[1]] || [];

        function atualizarIndices() {
            lista.querySelectorAll(".item-texto input").forEach(
                function (input, indice) {
                    input.name = `${propriedade}[${indice}]`;
                }
            );
        }

        function adicionarItem(valor = "") {
            const item = document.createElement("div");
            item.className = "item-texto";

            const input = document.createElement("input");
            input.type = "text";
            input.value = valor;
            input.placeholder = "Descreva o apoio ou adaptação";

            const remover = document.createElement("button");
            remover.type = "button";
            remover.className = "btn-remover";
            remover.textContent = "Remover";

            item.append(input, remover);
            lista.appendChild(item);

            atualizarIndices();
        }

        valores.forEach(function (valor) {
            adicionarItem(valor);
        });

        const botao = document.createElement("button");
        botao.type = "button";
        botao.className = "btn-adicionar";
        botao.textContent = "+ Adicionar item";

        botao.addEventListener("click", function () {
            adicionarItem();
        });

        lista.appendChild(botao);
    });

    /* Remoção de itens */

    formulario.addEventListener("click", function (evento) {
        if (!evento.target.classList.contains("btn-remover")) return;

        const item = evento.target.closest(
            ".item-dinamico, .barreira-item, .item-texto"
        );

        if (!item) return;

        item.remove();

        if (listaDatas) atualizarIndicesDatas();
        if (listaBarreiras) atualizarIndicesBarreiras();

        document.querySelectorAll(".lista-textos").forEach(function (lista) {
            lista.querySelectorAll(".item-texto input").forEach(
                function (input, indice) {
                    input.name = `${lista.dataset.name}[${indice}]`;
                }
            );
        });
    });

    /* Limpeza e reindexação antes do envio */

    formulario.addEventListener("submit", function () {
        document.querySelectorAll("#lista-datas .item-dinamico").forEach(
            function (item) {
                if (!item.querySelector("input").value) {
                    item.remove();
                }
            }
        );

        document.querySelectorAll("#lista-barreiras .barreira-item").forEach(
            function (item) {
                const campos = item.querySelectorAll("select, textarea");

                const preenchido = Array.from(campos).some(
                    campo => campo.value.trim() !== ""
                );

                if (!preenchido) {
                    item.remove();
                }
            }
        );

        document.querySelectorAll(".lista-textos").forEach(function (lista) {
            lista.querySelectorAll(".item-texto").forEach(function (item) {
                if (!item.querySelector("input").value.trim()) {
                    item.remove();
                }
            });
        });

        if (listaDatas) atualizarIndicesDatas();
        if (listaBarreiras) atualizarIndicesBarreiras();

        document.querySelectorAll(".lista-textos").forEach(function (lista) {
            lista.querySelectorAll(".item-texto input").forEach(
                function (input, indice) {
                    input.name = `${lista.dataset.name}[${indice}]`;
                }
            );
        });
    });
});
