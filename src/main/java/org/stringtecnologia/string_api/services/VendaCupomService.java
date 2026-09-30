package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import org.stringtecnologia.string_api.model.entities.Venda;
import org.stringtecnologia.string_api.model.entities.VendaItem;
import org.stringtecnologia.string_api.model.enums.StatusVenda;
import org.stringtecnologia.string_api.repository.VendaRepository;
import org.stringtecnologia.string_api.util.exceptions.venda.OperacaoVendaInvalidaException;
import org.stringtecnologia.string_api.util.exceptions.venda.VendaNaoEncontradaException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class VendaCupomService {

    private static final Locale LOCALE_BR =
            Locale.of("pt", "BR");

    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    private static final String EMPRESA_NOME =
            "STRING TECNOLOGIA & SOLUÇÕES";

    private static final String EMPRESA_ENDERECO =
            "Grande Colorado QD 01 CJ A LT 27 - Sobradinho II, Brasília - DF, 73105-900";

    private static final String EMPRESA_TELEFONE =
            "(61) 3302-3957";

    private final VendaRepository vendaRepository;


    @Transactional(readOnly = true)
    public String gerarCupom(
            Long vendaId
    ) {

        Venda venda =
                vendaRepository
                        .findById(vendaId)
                        .orElseThrow(
                                () ->
                                        new VendaNaoEncontradaException(
                                                vendaId
                                        )
                        );

        if (
                venda.getStatus()
                        != StatusVenda.FINALIZADA
        ) {

            throw new OperacaoVendaInvalidaException(
                    "Somente vendas finalizadas podem emitir o cupom."
            );
        }

        return montarHtml(venda);
    }


    private String montarHtml(
            Venda venda
    ) {

        StringBuilder itensHtml =
                new StringBuilder();

        int numeroItem = 1;

        for (
                VendaItem item :
                venda.getItens()
        ) {

            itensHtml.append(
                    montarItem(
                            numeroItem++,
                            item
                    )
            );
        }

        String clienteNome =
                venda.getCliente() != null
                        ? venda.getCliente().getNome()
                        : "VENDA DE BALCÃO";

        String clienteCpf =
                venda.getCliente() != null
                        ? venda.getCliente().getCpf()
                        : null;

        String cpfHtml =
                clienteCpf != null &&
                        !clienteCpf.isBlank()
                        ? """
                        <div class="linha-info">
                            <span>CPF:</span>
                            <strong>%s</strong>
                        </div>
                        """.formatted(
                        escapar(clienteCpf)
                )
                        : "";

        String observacaoHtml =
                venda.getObservacao() != null &&
                        !venda.getObservacao()
                                .isBlank()
                        ? """
                        <div class="separador"></div>

                        <div class="observacao">
                            <strong>Observações</strong>
                            <div>%s</div>
                        </div>
                        """.formatted(
                        escapar(
                                venda.getObservacao()
                        )
                )
                        : "";

        return """
                <!DOCTYPE html>
                <html lang="pt-BR">

                <head>

                    <meta charset="UTF-8">

                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1"
                    >

                    <title>%s</title>

                    <style>

                        @page {
                            size: 80mm auto;
                            margin: 3mm;
                        }

                        * {
                            box-sizing: border-box;
                        }

                        html,
                        body {
                            margin: 0;
                            padding: 0;
                            background: #fff;
                        }

                        body {
                            width: 74mm;
                            margin: 0 auto;
                            font-family:
                                "Courier New",
                                monospace;
                            font-size: 12px;
                            line-height: 1.4;
                            color: #000;
                            font-weight: 700;
                        }

                        .cabecalho {
                            text-align: center;
                            margin-bottom: 8px;
                        }

                        .empresa {
                            font-size: 17px;
                            font-weight: 900;
                            text-transform: uppercase;
                            line-height: 1.2;
                        }

                        .subempresa {
                            margin-top: 3px;
                            font-size: 12px;
                            font-weight: 800;
                        }

                        .empresa-info {
                            margin-top: 7px;
                            font-size: 10px;
                            font-weight: 800;
                            line-height: 1.35;
                            word-break: break-word;
                        }

                        .empresa-info div + div {
                            margin-top: 2px;
                        }

                        .documento {
                            margin-top: 8px;
                            font-size: 13px;
                            font-weight: 900;
                        }

                        .nao-fiscal {
                            margin-top: 4px;
                            font-size: 12px;
                            font-weight: 900;
                        }

                        .separador {
                            border-top: 1px dashed #000;
                            margin: 7px 0;
                        }

                        .linha-info {
                            display: flex;
                            justify-content: space-between;
                            align-items: flex-start;
                            gap: 8px;
                            font-weight: 800;
                        }

                        .linha-info span {
                            flex-shrink: 0;
                            font-weight: 800;
                        }

                        .linha-info strong {
                            flex: 1;
                            text-align: right;
                            font-weight: 900;
                            word-break: break-word;
                        }

                        .secao-titulo {
                            font-weight: 900;
                        }

                        .item {
                            margin-bottom: 7px;
                            font-weight: 800;
                        }

                        .item-descricao {
                            font-weight: 900;
                            word-break: break-word;
                        }

                        .item-valores {
                            display: flex;
                            justify-content: space-between;
                            gap: 8px;
                            margin-top: 2px;
                            font-weight: 800;
                        }

                        .item-detalhe {
                            color: #000;
                            font-weight: 800;
                        }

                        .item-valores strong {
                            font-weight: 900;
                        }

                        .totais {
                            margin-top: 4px;
                        }

                        .total {
                            display: flex;
                            justify-content: space-between;
                            gap: 8px;
                            margin-bottom: 4px;
                            font-weight: 800;
                        }

                        .total strong {
                            font-weight: 900;
                        }

                        .total-geral {
                            margin-top: 5px;
                            padding-top: 5px;
                            border-top: 1px solid #000;
                            font-size: 15px;
                            font-weight: 900;
                        }

                        .total-geral span,
                        .total-geral strong {
                            font-weight: 900;
                        }

                        .observacao {
                            font-weight: 800;
                            word-break: break-word;
                        }

                        .observacao strong {
                            display: block;
                            margin-bottom: 3px;
                            font-weight: 900;
                        }

                        .rodape {
                            margin-top: 12px;
                            text-align: center;
                            font-weight: 800;
                        }

                        .rodape strong {
                            display: block;
                            margin-bottom: 4px;
                            font-weight: 900;
                        }

                        .rodape div {
                            font-weight: 800;
                        }

                        @media print {

                            html,
                            body {
                                width: 74mm;
                            }

                        }

                    </style>

                </head>

                <body>

                    <div class="cabecalho">

                        <div class="empresa">
                            %s
                        </div>

                        <div class="subempresa">
                            Comprovante de venda
                        </div>

                        <div class="empresa-info">
                            <div>%s</div>
                            <div>Telefone: %s</div>
                        </div>

                        <div class="documento">
                            %s
                        </div>

                        <div class="nao-fiscal">
                            DOCUMENTO NÃO FISCAL
                        </div>

                    </div>


                    <div class="separador"></div>


                    <div class="linha-info">

                        <span>Data:</span>

                        <strong>
                            %s
                        </strong>

                    </div>


                    <div class="linha-info">

                        <span>Cliente:</span>

                        <strong>
                            %s
                        </strong>

                    </div>

                    %s


                    <div class="separador"></div>


                    <div class="secao-titulo">
                        ITENS
                    </div>


                    <div class="separador"></div>


                    %s


                    <div class="separador"></div>


                    <div class="totais">

                        <div class="total">

                            <span>
                                Subtotal
                            </span>

                            <strong>
                                %s
                            </strong>

                        </div>


                        <div class="total">

                            <span>
                                Desconto
                            </span>

                            <strong>
                                - %s
                            </strong>

                        </div>


                        <div class="total total-geral">

                            <span>
                                TOTAL
                            </span>

                            <strong>
                                %s
                            </strong>

                        </div>

                    </div>

                    %s


                    <div class="separador"></div>


                    <div class="rodape">

                        <strong>
                            Obrigado pela preferência!
                        </strong>

                        <div>
                            String Tecnologia & Soluções
                        </div>

                        <div>
                            %s
                        </div>

                    </div>

                </body>

                </html>
                """.formatted(
                escapar(venda.getNumero()),
                escapar(EMPRESA_NOME),
                escapar(EMPRESA_ENDERECO),
                escapar(EMPRESA_TELEFONE),
                escapar(venda.getNumero()),
                venda.getDataVenda(),
                escapar(clienteNome),
                cpfHtml,
                itensHtml,
                dinheiro(
                        venda.getValorSubtotal()
                ),
                dinheiro(
                        venda.getDesconto()
                ),
                dinheiro(
                        venda.getValorTotal()
                ),
                observacaoHtml,
                escapar(venda.getNumero())
        );
    }


    private String montarItem(
            int numero,
            VendaItem item
    ) {

        return """
                <div class="item">

                    <div class="item-descricao">
                        %02d - %s
                    </div>

                    <div class="item-valores">

                        <span class="item-detalhe">
                            %s x %s
                        </span>

                        <strong>
                            %s
                        </strong>

                    </div>

                </div>
                """.formatted(
                numero,
                escapar(
                        item.getDescricao()
                ),
                item.getQuantidade()
                        .stripTrailingZeros()
                        .toPlainString(),
                dinheiro(
                        item.getValorUnitario()
                ),
                dinheiro(
                        item.getValorTotal()
                )
        );
    }


    private String dinheiro(
            BigDecimal valor
    ) {

        BigDecimal seguro =
                valor != null
                        ? valor
                        : BigDecimal.ZERO;

        return NumberFormat
                .getCurrencyInstance(
                        LOCALE_BR
                )
                .format(seguro);
    }


    private String escapar(
            String valor
    ) {

        if (valor == null) {
            return "";
        }

        return HtmlUtils.htmlEscape(
                valor
        );
    }

}
