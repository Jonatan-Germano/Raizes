package com.raizes.model;
import com.raizes.dto.DadosAtualizacaoUsuario;
import com.raizes.dto.DadosCadastroUsuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity(name="Usuario")
@Table(name="usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Usuario {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    private String nome;

    @NotBlank
    @Column(unique=true)
    private String email;

    @NotBlank
    @Column(unique=true)
    @Size(min = 8, max = 8)
    private String telefone;

    private Boolean ativo;

    public Usuario(@Valid DadosCadastroUsuario dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.ativo = true;
    }

    public void atualizar(@Valid DadosAtualizacaoUsuario dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.email() != null) {
            this.email = dados.email();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
        if (dados.ativo() != null) {
            this.ativo = dados.ativo();
        }
    }
}
