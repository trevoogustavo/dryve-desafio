package com.dryve.dryvecarros.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public enum EnumMensagensErro {

    ERRO_AO_CONSULTAR_API_FIPE("001", "Erro ao tentar se comunicar com http://parallelum.com.br/"),
    VEICULO_CADASTRADO_NA_BASE("002","Veiculo já está cadastrado na base de dados!"),
    VEICULO_NAO_ENCONTRADO("003","Veiculo não está cadastrado "),
    MODELO_NAO_EXISTE("004","Este Modelo não existe na base de dados!"),
    ERRO_CONVERSAO_FIPE("005","Erro ao tentar converter o preço da FIPE");
    private String  codigo;
    private String  mensagem;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
