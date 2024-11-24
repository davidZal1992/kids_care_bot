package com.kids.keeper.demo.controller;

import com.kids.keeper.demo.bot.GatherHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioController {

    private final GatherHandler gatherHandler;

    public TwilioController(GatherHandler gatherHandler) {
        this.gatherHandler = gatherHandler;
    }

    @GetMapping("/gather")
    public String gatherResponse(@RequestParam("Digits") String digits, @RequestParam("Called") String called) {
        return gatherHandler.gatherResponse(digits, called);
    }

    @GetMapping("/noResponse/number/{escalatedNumber}")
    public void noResponse(@RequestParam(value = "CallStatus", required = false) String callStatus, @PathVariable String escalatedNumber) {
        gatherHandler.noResponse(callStatus, escalatedNumber);
    }
}

