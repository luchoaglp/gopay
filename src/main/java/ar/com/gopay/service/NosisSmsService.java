package ar.com.gopay.service;

import ar.com.gopay.domain.PaymentLink;
import ar.com.gopay.domain.nosis.Datos;
import ar.com.gopay.domain.nosis.Nosis;
import ar.com.gopay.domain.nosis.Resultado;
import ar.com.gopay.domain.nosis.Sms;
import ar.com.gopay.domain.nosispayment.NosisSms;
import ar.com.gopay.domain.nosispayment.NosisSmsState;
import ar.com.gopay.domain.nosispayment.NosisSmsValidation;
import ar.com.gopay.domain.nosispayment.NosisSmsEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

import static ar.com.gopay.domain.PaymentLinkState.OK;

@Slf4j
@Service
public class NosisSmsService {

    public void validateSms(PaymentLink paymentLink, Nosis nosis, String phone) {

        Resultado resultado = nosis.getContenido().getResultado();
        Datos datos = nosis.getContenido().getDatos();

        NosisSms nosisSms = paymentLink.getNosisSms();
        NosisSmsValidation nosisSmsValidation = null;

        if(nosisSms == null) {

            nosisSms = new NosisSms();

            nosisSmsValidation = nosisSms.getNosisSmsValidation();

            if (nosisSmsValidation == null) {

                nosisSmsValidation = new NosisSmsValidation();

            } else {

                nosisSms.setLastModifiedDate(new Date());
            }

        } else {

            nosisSmsValidation = nosisSms.getNosisSmsValidation();

            nosisSms.setLastModifiedDate(new Date());
        }

        Integer serverState = resultado.getEstado();

        nosisSmsValidation.setServerState(serverState);
        nosisSmsValidation.setServerDetail(resultado.getNovedad());
        nosisSms.setPhone(phone);

        if(serverState == 200) {

            Sms sms = datos.getSms();

            nosisSms.setSmsTx(datos.getConsultaId());
            nosisSms.setSmsLastState(sms.getState());
            nosisSmsValidation.setSmsDetail(sms.getNovedad());

            if(sms.getTokenEnviado() != null &&
                    sms.getTokenEnviado()) {

                nosisSmsValidation.setSmsSent(true);

            } else {

                nosisSmsValidation.setSmsSent(false);
            }
        }

        nosisSms.setNosisSmsValidation(nosisSmsValidation);
        paymentLink.setNosisSms(nosisSms);
    }

    public void evaluateSms(PaymentLink paymentLink, Nosis nosis) {

        Resultado resultado = nosis.getContenido().getResultado();
        Datos datos = nosis.getContenido().getDatos();

        NosisSms nosisSms = paymentLink.getNosisSms();
        NosisSmsEvaluation nosisSmsEvaluation;

        if(nosisSms == null) {

            return;

        } else {

            nosisSmsEvaluation = nosisSms.getNosisSmsEvaluation();

            if (nosisSmsEvaluation == null) {

                nosisSmsEvaluation = new NosisSmsEvaluation();

            }

            nosisSms.setLastModifiedDate(new Date());
        }

        Integer serverState = resultado.getEstado();

        nosisSmsEvaluation.setServerState(serverState);
        nosisSmsEvaluation.setServerDetail(resultado.getNovedad());

        if(serverState == 200) {

            Sms sms = datos.getSms();

            NosisSmsState lastState = sms.getState();

            nosisSms.setSmsLastState(lastState);

            /*
            if(lastState.equals("APROBADO")) {
                paymentLink.setState(OK);
            }
            */
        }

        nosisSms.setNosisSmsEvaluation(nosisSmsEvaluation);
        paymentLink.setNosisSms(nosisSms);
    }


}
