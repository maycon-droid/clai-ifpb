
package br.ifpb.edu.clai.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "estudos_de_caso")
public class EstudoDeCaso {

    @Id
    private String id;

    @NotBlank(message = "O aluno vinculado é obrigatório")
    private String alunoId;

    private List<LocalDate> datasAtendimentoAnamnese;

    private String profissionalResponsavel;
}
