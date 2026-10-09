package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import org.stringtecnologia.string_api.model.enums.TipoPessoa;

import java.time.Instant;

@Entity
@Table(name = "tb_fornecedor", uniqueConstraints = @UniqueConstraint(name = "uk_tb_fornecedor_cpf_cnpj", columnNames = "cpf_cnpj"))
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fornecedor_seq")
    @SequenceGenerator(name = "fornecedor_seq", sequenceName = "seq_tb_fornecedor", allocationSize = 1)
    @Column(name = "fornecedor_id")
    private Long fornecedorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 2)
    private TipoPessoa tipoPessoa;

    @Column(name = "razao_social", nullable = false, length = 200)
    private String razaoSocial;

    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Column(name = "cpf_cnpj", nullable = false, length = 14)
    private String cpfCnpj;

    @Column(name = "inscricao_estadual", length = 30)
    private String inscricaoEstadual;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "whatsapp", length = 20)
    private String whatsapp;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "endereco", length = 200)
    private String endereco;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "observacao", columnDefinition = "text")
    private String observacao;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private Instant dataCadastro;

    @Column(name = "data_atualizacao", nullable = false)
    private Instant dataAtualizacao;

    @PrePersist
    void prePersist() {
        Instant agora = Instant.now();
        dataCadastro = agora;
        dataAtualizacao = agora;
    }

    @PreUpdate
    void preUpdate() {
        dataAtualizacao = Instant.now();
    }

    public Long getFornecedorId() { return fornecedorId; }
    public TipoPessoa getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(TipoPessoa tipoPessoa) { this.tipoPessoa = tipoPessoa; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getInscricaoEstadual() { return inscricaoEstadual; }
    public void setInscricaoEstadual(String inscricaoEstadual) { this.inscricaoEstadual = inscricaoEstadual; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public Instant getDataCadastro() { return dataCadastro; }
    public Instant getDataAtualizacao() { return dataAtualizacao; }
}
