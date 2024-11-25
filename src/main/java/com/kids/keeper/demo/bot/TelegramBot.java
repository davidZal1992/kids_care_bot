package com.kids.keeper.demo.bot;

import com.kids.keeper.demo.bot.enums.Labels;
import com.kids.keeper.demo.bot.twilo.TwilioService;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kids.keeper.demo.bot.MessageTemplates.INITIAL_PHONE_CALL_DROP_MSG;
import static com.kids.keeper.demo.bot.MessageTemplates.INITIAL_PHONE_CALL_PICK_MSG;

@Slf4j
@Singleton
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

    @Value("${bot.father_chat_id}")
    private String fatherChatId;

    @Value("${bot.mom_chat_id}")
    private String momChatId;

    @Value("${bot.chat_id}")
    private String fatherMomGroupChatId;

    @Value("${phone.mother}")
    private String PHONE_MOTHER;

    @Value("${phone.father}")
    private String PHONE_FATHER;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private TwilioService twilioService;

    private static final Map<String, String> NAME_CONVERTOR = new HashMap<>();

    static {
        NAME_CONVERTOR.put("David", "דוד");
        NAME_CONVERTOR.put("Lior", "ליאור");
    }

    private InlineKeyboardMarkup pickUpKeyboardMarkup;
    private InlineKeyboardMarkup dropOffKeyboardMarkup;
    private Boolean answerReceived = false;
    private final String MOM_NAME = "Lior";
    private final String FATHER_NAME = "David";


    @PostConstruct
    public void init() {
        pickUpKeyboardMarkup = buildKeyboard(Labels.APPROVE_PICK_UP.name());
        dropOffKeyboardMarkup = buildKeyboard(Labels.APPROVE_DROP_OFF.name());
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }


    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            onCallBackReceived(update);
        }
    }

    private void onCallBackReceived(Update update) {
        this.answerReceived = true;
        String origin = update.getCallbackQuery().getFrom().getFirstName();

        log.info("Origin sender is: {} and the label is {}", origin, update.getCallbackQuery().getData());

        // Check if the callback data is for pickup

        if (update.getCallbackQuery().getData().equals(Labels.APPROVE_PICK_UP.name())) {

            if (origin.equals(convertNameToHebrew(MOM_NAME))) {
                log.info("Message received from father regarding pickup and it's approved");
                SendMessage sendMessage = buildResMessage(momChatId,
                        MessageTemplates.THANKS_MESSAGE_MSG);
                sendQuestion(sendMessage);
            }


            if (origin.equals(convertNameToHebrew(FATHER_NAME))) {
                log.info("Message received from father regarding pickup and it's approved");
                SendMessage sendMessage = buildResMessage(fatherChatId,
                        MessageTemplates.THANKS_MESSAGE_MSG);
                sendQuestion(sendMessage);

            }

            SendMessage message = buildResMessage(fatherMomGroupChatId,
                    String.format(MessageTemplates.APPROVED_PICK_KIDS_MSG, convertNameToHebrew(update.getCallbackQuery().getFrom().getFirstName())));
            sendQuestion(message);
        }

        // Check if the callback data is for drop off

        if (update.getCallbackQuery().getData().equals(Labels.APPROVE_DROP_OFF.name())) {

            if (origin.equals(MOM_NAME)) {
                log.info("Message received from mother regarding drop off and it's approved");
                SendMessage sendMessage = buildResMessage(momChatId,
                        MessageTemplates.THANKS_MESSAGE_MSG);
                sendQuestion(sendMessage);
            }

            if (origin.equals(FATHER_NAME)) {
                log.info("Message received from father regarding drop off and it's approved");
                SendMessage sendMessage = buildResMessage(fatherChatId,
                        MessageTemplates.THANKS_MESSAGE_MSG);
                sendQuestion(sendMessage);
            }

            SendMessage sendMessage = buildResMessage(fatherMomGroupChatId,
                    String.format(MessageTemplates.APPROVED_DROPPED_KIDS_MSG, convertNameToHebrew(update.getCallbackQuery().getFrom().getFirstName())));
            sendQuestion(sendMessage);
        }
    }


    private SendMessage buildMReqMessage(String chatId, String inputText, InlineKeyboardMarkup keyboardMarkup) {
        log.info("Building message for approval response to  chatId: {} and the text is: {}", chatId, inputText);
        return SendMessage.builder()
                .chatId(chatId)
                .text(inputText)
                .replyMarkup(keyboardMarkup)
                .build();
    }

    private SendMessage buildResMessage(String chatId, String inputText) {
        log.info("Building message for approval response to  chatId: {} and the text is: {}", chatId, inputText);
        return SendMessage.builder()
                .chatId(chatId)
                .text(inputText)
                .build();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    String translatedMomName = convertNameToHebrew(MOM_NAME);
    String translatedFatherName = convertNameToHebrew(FATHER_NAME);

    @Scheduled(cron = "0 45 07 ? * MON,WED,SUN", zone = "Asia/Jerusalem")
    // This will run every Monday, Wednesday and Sunday at 7:35 AM for David's drop the kids
    public void sendDailyQuestionFatherDrop() {
        log.info("Preparing message template for dropping the kids in kinder garden for David.");
        SendMessage sendMessage = buildMReqMessage(fatherChatId, String.format(MessageTemplates.REMINDER_MESSAGE_INBOUND_MSG, translatedFatherName), dropOffKeyboardMarkup);
        wrapMessageWithRetires(sendMessage, PHONE_FATHER, translatedFatherName, INITIAL_PHONE_CALL_DROP_MSG, PHONE_MOTHER);
    }

    @Scheduled(cron = "0 45 16 ? * TUE,THU", zone = "Asia/Jerusalem")
    // This will run every Tuesday and Thursday at 16:35 AM for David's pickup kids
    public void sendDailyQuestionFatherPick() {
        log.info("Preparing message template for taking the kids from kinder garden for Lior.");
        SendMessage sendMessage = buildMReqMessage(fatherChatId, String.format(MessageTemplates.REMINDER_MESSAGE_OUTBOUND_MSG, translatedFatherName), pickUpKeyboardMarkup);
        wrapMessageWithRetires(sendMessage, PHONE_FATHER, translatedFatherName, INITIAL_PHONE_CALL_PICK_MSG, PHONE_MOTHER);
    }

    @Scheduled(cron = "0 45 07 ? * TUE,THU", zone = "Asia/Jerusalem")
    // This will run every Tuesday and Thursday at 07:35 AM for Lior's drop kids
    public void sendDailyQuestionMomDrop() {
        log.info("Preparing message template for taking the kids from kinder garden for Lior.");
        SendMessage sendMessage = buildMReqMessage(momChatId, String.format(MessageTemplates.REMINDER_MESSAGE_INBOUND_MSG, translatedMomName), dropOffKeyboardMarkup);
        wrapMessageWithRetires(sendMessage, PHONE_MOTHER, translatedMomName, INITIAL_PHONE_CALL_DROP_MSG, PHONE_FATHER);
    }

    @Scheduled(cron = "0 45 16 ? * MON,WED,SUN", zone = "Asia/Jerusalem")
    // This will run every Monday, Wednesday and Sunday at 16:45 PM for Lior's drop the kids
    public void sendDailyQuestionMomPick() {
        log.info("Preparing message template for taking the kids from kinder garden for Lior.");
        SendMessage sendMessage = buildMReqMessage(momChatId, String.format(MessageTemplates.REMINDER_MESSAGE_OUTBOUND_MSG, translatedMomName), pickUpKeyboardMarkup);
        wrapMessageWithRetires(sendMessage, PHONE_MOTHER, translatedMomName, INITIAL_PHONE_CALL_PICK_MSG, PHONE_FATHER);
    }

    private void wrapMessageWithRetires(SendMessage message, String toCall, String name, String phoneInTextSpeech, String escalationNumber) {
        AtomicInteger retries = new AtomicInteger(0);
        taskScheduler.scheduleAtFixedRate(() -> {
            if (answerReceived) {
                log.info("Answer received, stopping retries.");
                taskScheduler.shutdown();
                return;
            }

            log.info("Retrying sending message attempts number {}", retries.get());

            if (retries.incrementAndGet() > 3) {
                taskScheduler.shutdown();
                SendMessage callToPhoneNumber = SendMessage.builder()
                        .chatId(fatherMomGroupChatId)
                        .text(String.format(MessageTemplates.NO_RESPONSE_AFTER_RETRIES_MSG, name, name))
                        .build();

                sendQuestion(callToPhoneNumber);

                log.info("After {} attempts no one answered. Going to call to phone number: {}", retries.get(), toCall);
                twilioService.initialCall(toCall, phoneInTextSpeech, escalationNumber);

            } else {
                sendQuestion(message);
            }
        }, Duration.ofSeconds(300));
    }

    private InlineKeyboardMarkup buildKeyboard(String label) {
        final String APPROVAL_LABEL = "מאשר/ת";
        log.info("Building keyboard for approval with label {}", label);

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(InlineKeyboardButton.builder()
                        .text(APPROVAL_LABEL)
                        .callbackData(label).build())))
                .build();
    }


    void sendQuestion(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertNameToHebrew(String englishName) {
        return NAME_CONVERTOR.getOrDefault(englishName, englishName);  // Return the English name if not found in the map
    }
}
