package com.kids.keeper.demo.bot;

public class MessageTemplates {
    public static final String REMINDER_MESSAGE_INBOUND_MSG = "בוקר טוב %s, תאשר/י בבקשה ששמת את איתי ואלמה בגן";
    public static final String REMINDER_MESSAGE_OUTBOUND_MSG = "אחר הצהריים טובים %s, תאשר/י בבקשה שאספת את איתי ואלמה מהגן";

    public static final String APPROVED_DROPPED_KIDS_MSG = "%s אישר/ה שהוריד/ה את הילדים בבטחה לגן";

    public static final String APPROVED_PICK_KIDS_MSG = "%s אישר/ה שאסף/ה את הילדים בבטחה מהגן";

    public static final String THANKS_MESSAGE_MSG = "תשובתך התקבלה";

    public static final String INITIAL_PHONE_CALL_PICK_MSG = "זוהי הודעה מוקלטת. שיחה זו התקבלה כי לא אישרתם בהודעה שהילדים הגיעו לגן בבטחה. אנא לחצו 1 לאישור";

    public static final String INITIAL_PHONE_CALL_DROP_MSG = "זוהי הודעה מוקלטת. שיחה זו התקבלה כי לא אישרתם בהודעה שהילדים נאספו מהגן בבטחה. אנא לחצו 1 לאישור";

    public static final String NO_RESPONSE_AFTER_RETRIES_MSG = "לא התקבל אישור מ%s לאחר 3 נסיונות להשיגו. הולך לנסות לחייג למספר טלפון של %s";

    public static final String NO_RESPONSE_IN_THE_PHONE_MSG = ".לא התקבלה תגובה, הולך לחייג לטלפון הבא שמוגדר ברשימה";

    public static final String PHONE_APPROVED_MESSAGE_MSG = "התקבל אישור טלפוני מ-%s שהכל תקין";

    public static final String THANKS_FOR_THE_RESPONSE_MSG = "תגובתך נקלטה בהצלחה";

    public static final String RETRY_DIGIT_MSG = "הקלט שהוזן שגוי. אנא הקש 1 אם אתה מאשר שהילדים במקום בטוח";

    public static final String ESCELATION_MESSAGE_MSG = "אתה מקבל שיחה זו כי אינך מוגדר איש קשר ברשימה במידה ואין מענה לגבי אישור מיקום הילדים, אני דאג לבדוק ולאשר את מיקומם";
}
