package com.siliconst.residentcare.Models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

public class SmsLoginResponse {
    @Root(name = "corpsms")
    public class Food {
        @Element(name = "command")
        String command;

        @Element(name = "data")
        String data;
        @Element(name = "response")
        String response;

    }


}
