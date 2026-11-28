package br.ifpb.edu.clai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "peas")
public class Pea {

    @Id
    private String id;

    @NotBlank(message = "O PEI vinculado é obrigatório")
    private String peiId;

    @NotBlank(message = "O componente curricular é obrigatório")
    @Size(max = 255, message = "O componente curricular deve ter no máximo 255 caracteres")
    private String componenteCurricular;

    @NotBlank(message = "O semestre/ano é obrigatório")
    @Size(max = 50, message = "O semestre/ano deve ter no máximo 50 caracteres")
    private String semestreAno;

    @NotBlank(message = "O docente responsável é obrigatório")
    @Size(max = 255, message = "O docente deve ter no máximo 255 caracteres")
    private String docente;

    @NotBlank(message = "O objetivo geral é obrigatório")
    @Size(max = 5000, message = "O objetivo geral deve ter no máximo 5000 caracteres")
    private String objetivoGeral;

    @NotBlank(message = "Os objetivos específicos são obrigatórios")
    @Size(max = 5000, message = "Os objetivos específicos devem ter no máximo 5000 caracteres")
    private String objetivosEspecificos;

    @NotBlank(message = "Os conteúdos programáticos são obrigatórios")
    @Size(max = 5000, message = "Os conteúdos programáticos devem ter no máximo 5000 caracteres")
    private String conteudosProgramaticos;

    @NotBlank(message = "A metodologia é obrigatória")
    @Size(max = 5000, message = "A metodologia deve ter no máximo 5000 caracteres")
    private String metodologia;

    @NotBlank(message = "A avaliação é obrigatória")
    @Size(max = 5000, message = "A avaliação deve ter no máximo 5000 caracteres")
    private String avaliacao;

    @Size(max = 5000, message = "A bibliografia básica deve ter no máximo 5000 caracteres")
    private String bibliografiaBasica;

    @Size(max = 5000, message = "A bibliografia complementar deve ter no máximo 5000 caracteres")
    private String bibliografiaComplementar;

    @Size(max = 255, message = "A assinatura do docente deve ter no máximo 255 caracteres")
    private String assinaturaDocente;
}
