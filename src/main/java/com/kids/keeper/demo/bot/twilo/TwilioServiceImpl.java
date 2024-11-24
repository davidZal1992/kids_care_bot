package com.kids.keeper.demo.bot.twilo;

import com.kids.keeper.demo.bot.MessageTemplates;
import com.twilio.Twilio;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.CallCreator;
import com.twilio.twiml.VoiceResponse;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;
import com.twilio.type.Twiml;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TwilioServiceImpl implements TwilioService {

    @Value("${twilio.account_sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth_token}")
    private String AUTH_TOKEN;

    @Value("${twilio.from}")
    private String FROM_NUMBER;

    @Value("${twilio.call_back}")
    private String CALL_BACK;

    @PostConstruct
    public void init() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        initialCall("+972542020184", "שלום זוהי בדיקה של המערכת", "972542020184");
    }

    /**
     * This method is used to make a call to the initial number
     *
     * @param toNumber         - Destination number
     * @param text             - Message to be played
     * @param escalationNumber - Escalation phone  number
     */
    @Override
    public void initialCall(String toNumber, String text, String escalationNumber) {

        VoiceResponse voiceResponse = new VoiceResponse.Builder()
                .gather(buildGather(text))
                .say(buildSay(MessageTemplates.NO_RESPONSE_IN_THE_PHONE_MSG))
                .build();

        log.info("Start preparing initial call to the number: {}", toNumber);

        try {
            createCall(voiceResponse, toNumber)
                    .setMethod(HttpMethod.GET)
                    .setStatusCallback(URI.create(CALL_BACK + "/noResponse/number/" + escalationNumber))
                    .setStatusCallbackMethod(HttpMethod.GET)
                    .setStatusCallbackEvent(List.of(Call.Status.COMPLETED.toString()))
                    .create();
        } catch (Exception e) {
            log.info("Error while making call to the number: {}", toNumber);
            e.printStackTrace();
        }
    }

    /**
     * This method is used to make a call to the escalation number
     *
     * @param toNumber - Destination number
     */
    @Override
    public void escalationCall(String toNumber) {
        VoiceResponse voiceResponse = new VoiceResponse.Builder()
                .say(buildSay(String.format(MessageTemplates.ESCELATION_MESSAGE_MSG)))
                .build();

        log.info("Start preparing escalation call to the number: {}", toNumber);

        try {
            createCall(voiceResponse, "+" + toNumber)
                    .create();
        } catch (Exception e) {
            log.info("Error while making call to the number: {}", toNumber);
            e.printStackTrace();
        }
    }


    private CallCreator createCall(VoiceResponse voiceResponse, String toNumber) {
        return Call.creator(
                new PhoneNumber(toNumber),
                new PhoneNumber(FROM_NUMBER),
                new Twiml(voiceResponse.toXml()));
    }

    private Say buildSay(String message) {
        return new Say.Builder(message)
                .language(Say.Language.HE_IL)
                .build();
    }

    private Gather buildGather(String text) {
        return new Gather.Builder()
                .action(CALL_BACK + "/gather")
                .method(HttpMethod.GET)
                .timeout(10)
                .numDigits(1)
                .say(buildSay(text))
                .build();
    }
}

