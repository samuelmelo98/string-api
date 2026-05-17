package org.stringtecnologia.string_api.integration.infosimples.adapter;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.stringtecnologia.string_api.integration.infosimples.client.InfosimplesApiClient;
import org.stringtecnologia.string_api.integration.infosimples.config.InfosimplesApiProperties;
import org.stringtecnologia.string_api.integration.infosimples.dto.CpfResultResponseDTO;
import org.stringtecnologia.string_api.integration.infosimples.dto.RestricaoSolicitacaoResponseDTO;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfosimplesCpfAdapter {

    private final InfosimplesApiClient client;
    private final InfosimplesApiProperties props;

    public InfosimplesCpfAdapter(InfosimplesApiClient client, InfosimplesApiProperties props) {
        this.client = client;
        this.props = props;
    }

    public RestricaoSolicitacaoResponseDTO buscarPorCpf(String cpf, String dataNascimentoIso) {

        // Certifique-se de que o tipo da esquerda e da direita usam MultiValueMap / LinkedMultiValueMap
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cpf", cpf);
        params.add("birthdate", dataNascimentoIso);
        params.add("token", props.apiKey());
        params.add("timeout", "300");

        // Agora o compilador vai aceitar a passagem do parâmetro sem reclamar de tipos incompatíveis
        var response = client.consultarCpf(params);

        if (response == null || response.code() != 200 || response.data() == null || response.data().isEmpty()) {
            return null;
        }

        CpfResultResponseDTO item = response.data().get(0);
        return toResponseDTO(item);
    }

    private RestricaoSolicitacaoResponseDTO toResponseDTO(CpfResultResponseDTO item) {
        boolean ativoConvertido = false;

        // Regra flexível para converter o status cadastral da Receita em boolean ativo, se necessário
        if (item.situacaoCadastral() != null) {
            ativoConvertido = item.situacaoCadastral().equalsIgnoreCase("REGULAR")
                    || item.situacaoCadastral().equalsIgnoreCase("ATIVA");
        }

        // Limpa o CPF tirando os pontos e traço se quiser converter para Long, ou use o hash do CPF como ID temporário
        Long idMapeado = null;
        if (item.cpf() != null) {
            try {
                idMapeado = Long.parseLong(item.cpf().replaceAll("[^0-8]", ""));
            } catch (NumberFormatException ignored) {}
        }

        return new RestricaoSolicitacaoResponseDTO(
                idMapeado,
                item.nome(),
                item.cpf(), // ou matricula se aplicar no seu caso
                ativoConvertido,
                false
        );
    }
}