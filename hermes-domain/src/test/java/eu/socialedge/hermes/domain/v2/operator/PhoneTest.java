package eu.socialedge.hermes.domain.v2.operator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class PhoneTest {
    private static final Collection<String> GOOD_PHONE_NUMS = new ArrayList<String>() {{
        add("+1 1234567890123");
        add("+12 123456789");
        add("+123 123456");
    }};

    private static final Collection<String> BAD_PHONE_NUMS = new ArrayList<String>() {{
        add("1 1234567890123");
        add("2+1234567890123");
        add("++1234567890123");
        add("+1 234567890123+");
    }};

    @Test
    public void testGoodPhoneNumbers() {
        GOOD_PHONE_NUMS.forEach(Phone::new);
    }

    @Test
    public void testBadPhoneNumbers() {
        int badCounter = 0;

        for (String phoneNum : BAD_PHONE_NUMS) {
            try {
                new Phone(phoneNum);
            } catch (Exception e) {
                badCounter++;
            }
        }

        assertEquals(BAD_PHONE_NUMS.size(), badCounter);
    }

    @Test
    public void testGetRawMessage() {
        GOOD_PHONE_NUMS.forEach(phoneNum -> {
            assertEquals(phoneNum, new Phone(phoneNum).number());
        });
    }
}