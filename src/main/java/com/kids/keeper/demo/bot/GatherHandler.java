package com.kids.keeper.demo.bot;

import com.kids.keeper.demo.bot.twilo.TwilioService;
import com.twilio.http.HttpMethod;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Redirect;
import com.twilio.twiml.voice.Say;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class GatherHandler {

    @Value("${bot.chat_id}")
    private String channelId;

    @Value("${twilio.call_back}")
    private String CALL_BACK;

    private final AtomicBoolean alreadyEscalated = new AtomicBoolean(false);

    private final TwilioService twilioService;

    @Autowired
    private final TelegramBot telegramBot;

    public GatherHandler(TelegramBot telegramBot, TwilioService twilioService) {
        this.telegramBot = telegramBot;
        this.twilioService = twilioService;
    }

    private static final Map<String, String> PHONE_CONVERTOR = new HashMap<>();

    static {
        PHONE_CONVERTOR.put("972542020184", "דוד");
        PHONE_CONVERTOR.put("972528876886", "ליאור");
    }


    /**
     * This method is used to make a call to the escalation number
     *
     * @param escalatedNumber -  Escalation phone number to be called
     */
    public void noResponse(String callStatus, String escalatedNumber) {
        System.out.println(alreadyEscalated);

        if (alreadyEscalated.get()) {
            alreadyEscalated.set(false);
            return;
        }

        if (callStatus.equals("no-answer")) {
            twilioService.escalationCall(escalatedNumber);
            alreadyEscalated.set(true);
        }
    }

    /**
     * This method is used to gather the response from the user
     *
     * @param digits - Digits pressed by the user
     * @param called - Number called
     */
    public String gatherResponse(String digits, String called) {

        System.out.println("Digits: " + digits);
        if (digits.equals("1")) {
            log.info("User approved  by clicking on '1', user name: {}. Will send approval message to telegram", called);

            String voiceResponse = new VoiceResponse.Builder()
                    .say(sayBuilder(MessageTemplates.THANKS_FOR_THE_RESPONSE_MSG))
                    .build()
                    .toXml();

            //Sending message to the telegram group
            SendMessage sendMessage = SendMessage.builder().chatId(channelId)
                    .text(String.format(MessageTemplates.PHONE_APPROVED_MESSAGE_MSG, convertPhoneToName(called))).build();

            telegramBot.sendQuestion(sendMessage);

            return voiceResponse;

        } else if (digits.isEmpty() || digits.isBlank()) {

            System.out.println("I A M HERE ");
            return new VoiceResponse.Builder()
                    .say(sayBuilder(String.format(MessageTemplates.NO_RESPONSE_IN_THE_PHONE_MSG)))
                    .redirect(new Redirect.Builder(CALL_BACK + "/noResponse?Called=" + called).method(HttpMethod.GET).build())
                    .build()
                    .toXml();
        } else {
            return new VoiceResponse.Builder()
                    .gather(gatherBuilder())
                    .say(sayBuilder(String.format(MessageTemplates.NO_RESPONSE_IN_THE_PHONE_MSG)))
                    .build()
                    .toXml();
        }

    }

    private Gather gatherBuilder() {
        return new Gather.Builder()
                .action(CALL_BACK + "/gather")
                .method(HttpMethod.GET)
                .timeout(10)
                .numDigits(1)
                .say(sayBuilder(MessageTemplates.RETRY_DIGIT_MSG))
                .build();
    }

    private Say sayBuilder(String message) {
        return new Say.Builder(message)
                .language(Say.Language.HE_IL)
                .build();
    }

    private String convertPhoneToName(String phone) {
        return PHONE_CONVERTOR.get(phone);  // Return the English name if not found in the map
    }
}
