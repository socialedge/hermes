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
package eu.socialedge.hermes.domain.operator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class EmailTest {
    private static final Collection<String> GOOD_EMAILS = new ArrayList<String>() {{
        add("user@domain.com");
        add("user@domain.co.in");
        add("user.name@domain.com");
        add("user_name@domain.com");
        add("username@yahoo.corporate.in");
    }};

    private static final Collection<String> BAD_EMAILS = new ArrayList<String>() {{
        add(".username@yahoo.com");
        add("username@yahoo.com.");
        add("username@yahoo..com");
        add("username@yahoo.c");
        add("username@yahoo.corporate");
    }};

    @Test
    public void shallCreateEmailObjsIfAddressIsValid() {
        GOOD_EMAILS.forEach(Email::new);
    }

    @Test
    public void shallNotCreateEmailObjsIfAddressIsInvalid() {
        int badCounter = 0;

        for (String email : BAD_EMAILS) {
            try {
                new Email(email);
            } catch (Exception e) {
                badCounter++;
            }
        }

        assertEquals(BAD_EMAILS.size(), badCounter);
    }

    @Test
    public void shallReturnUnchangedValueAfterEmailBoxing() {
        GOOD_EMAILS.forEach(emailAddress -> {
            assertEquals(emailAddress, new Email(emailAddress).address());
        });
    }

    @Test
    public void shouldReturnValidLocalAndDomainEmailParts() {
        final String emailAddress = "user_name@domain.com";
        final String emailAddressMailboxName = "user_name";
        final String emailAddressDomainName = "domain.com";

        Email email = new Email(emailAddress);

        assertEquals(emailAddressMailboxName, email.mailboxName());
        assertEquals(emailAddressDomainName, email.domainName());
    }
}