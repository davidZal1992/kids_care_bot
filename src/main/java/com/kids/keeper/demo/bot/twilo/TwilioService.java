package com.kids.keeper.demo.bot.twilo;

public interface TwilioService {

    void initialCall(String toNumber, String text, String escalationNumber);

    void escalationCall(String toNumber);
}
