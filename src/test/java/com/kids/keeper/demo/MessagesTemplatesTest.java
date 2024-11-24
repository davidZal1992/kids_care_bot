package com.kids.keeper.demo;

import com.kids.keeper.demo.bot.MessageTemplates;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MessagesTemplatesTest {


    @Test
    public void correctMessageReturnedSendInitialInboundReminderMessageToCorrectParent() {
        String actualMessage = String.format(MessageTemplates.REMINDER_MESSAGE_INBOUND_MSG, "דוד");
        String expected = "בוקר טוב דוד, תאשר/י בבקשה ששמת את איתי ואלמה בגן";
        assertEquals(expected, actualMessage);
    }

    @Test
    public void correctMessageReturnedSendInitialOutboundReminderMessageToCorrectParent() {
        String actualMessage = String.format(MessageTemplates.REMINDER_MESSAGE_OUTBOUND_MSG, "דוד");
        String expected = "אחר הצהריים טובים דוד, תאשר/י בבקשה שאספת את איתי ואלמה מהגן";
        assertEquals(expected, actualMessage);
    }

    @Test
    public void correctMessageReturnedSendApprovalPickMessageToCorrectParent() {
        String actualMessage = String.format(MessageTemplates.APPROVED_DROPPED_KIDS_MSG, "דוד");
        String expected = "דוד אישר/ה שהוריד/ה את הילדים בבטחה לגן";
        assertEquals(expected, actualMessage);
    }

    @Test
    public void correctMessageReturnedSendApprovalDropMessageToCorrectParent() {
        String actualMessage = String.format(MessageTemplates.APPROVED_PICK_KIDS_MSG, "דוד");
        String expected = "דוד אישר/ה שאסף/ה את הילדים בבטחה מהגן";
        assertEquals(expected, actualMessage);
    }

    @Test
    public void correctMessageReturnedSendAfterRetriesNotAnswer() {
        String actualMessage = String.format(MessageTemplates.NO_RESPONSE_AFTER_RETRIES_MSG, "דוד", "דוד");
        String expected = "לא התקבל אישור מדוד לאחר 3 נסיונות להשיגו. הולך לנסות לחייג למספר טלפון של דוד";
        assertEquals(expected, actualMessage);
    }

    @Test
    public void correctMessageReturned_UserApprovedTheCall_SendCorrectApprovalMessage() {
        String actualMessage = String.format(MessageTemplates.PHONE_APPROVED_MESSAGE_MSG, "דוד");
        String expected = "התקבל אישור טלפוני מ-דוד שהכל תקין";
        assertEquals(expected, actualMessage);
    }

}
