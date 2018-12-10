package ar.com.gopay.service;

import ar.com.gopay.domain.Client;
import ar.com.gopay.domain.nosis.ws1.Nosis;
import ar.com.gopay.exception.RestTemplateResponseErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NosisService {

    @Autowired
    private RestTemplateBuilder builder;

    @Value("${nosis.user}")
    private String nosisUser;

    @Value("${nosis.token}")
    private String nosisToken;

    @Value("${nosis.ws1}")
    private String nosisWs1;

    public Nosis getNosisByClient(Client client) {

        RestTemplate restTemplate = builder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();

        StringBuilder nosisUrl = new StringBuilder();

        nosisUrl.append(nosisWs1 + "/variables?")
                .append("Usuario=").append(nosisUser)
                .append("&Token=").append(nosisToken)
                .append("&Documento=").append(client.getDni())
                .append("&Format=JSON")
                .append("&VR=VI_Nombre,VI_Apellido,CI_Vig_PeorSit,CI_Vig_Total_CantBcos,CI_12m_PeorSit,VR_Vig_CapacidadEndeu_Deuda,SCO_Vig");

        return restTemplate.exchange(nosisUrl.toString(),
                HttpMethod.GET, null,
                Nosis.class).getBody();
    }
}