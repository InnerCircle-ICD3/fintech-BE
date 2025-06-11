package com.fastcampus.payment.parksay;

import org.junit.jupiter.api.Test;

public class SimpleTest {

    @Test
    public void ttest() {
        String target = "0123-4567-8910-1112";
        int lastIdx = target.lastIndexOf("-");
        String result = target.substring(lastIdx);
        System.out.println("result = " + result);
    }
}
