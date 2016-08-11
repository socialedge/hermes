/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package eu.socialedge.hermes.domain.contact;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class PhoneTest {
    private static final Collection<String> GOOD_PHONE_NUMS = Arrays.asList("+1 1234567890123", "+12 123456789",
            "+123 123456");

    private static final Collection<String> BAD_PHONE_NUMS = Arrays.asList("1 1234567890123", "2+1234567890123",
            "++1234567890123", "+1 234567890123+");

    @Test
    public void shallCreatePhoneObjsIfNumbersAreValid() {
        GOOD_PHONE_NUMS.forEach(Phone::new);
    }

    @Test
    public void shallNotCreatePhoneObjsIfNumbersAreInvalid() {
        BAD_PHONE_NUMS.forEach(phone -> {
            try {
                new Phone(phone);
                fail("Illegal argument exception expected for incorrect phone: " + phone);
            } catch (IllegalArgumentException e) {
            } catch (Exception e1) {
                fail("Illegal argument exception expected for incorrect phone: " + phone);
            }
        });
    }

    @Test
    public void shallReturnUnchangedValueAfterPhoneNumberBoxing() {
        GOOD_PHONE_NUMS.forEach(phoneNum -> {
            assertEquals(phoneNum, new Phone(phoneNum).number());
        });
    }
}