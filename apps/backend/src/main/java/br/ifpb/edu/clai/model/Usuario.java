package br.ifpb.edu.clai.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    @NotBlank
    private String nome;

    @Indexed(unique = true)
    @NotBlank
    private String login;

    @JsonIgnore
    @NotBlank
    private String senhaHash;

    @NotNull
    private PerfilUsuario perfil;

    private boolean ativo;
}