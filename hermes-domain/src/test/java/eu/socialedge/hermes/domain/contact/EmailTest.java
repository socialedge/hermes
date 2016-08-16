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

public class EmailTest {
    private static final Collection<String> GOOD_EMAILS = Arrays.asList("user@domain.com", "user@domain.co.in",
            "user.name@domain.com", "user_name@domain.com", "username@yahoo.corporate.in");


    private static final Collection<String> BAD_EMAILS = Arrays.asList((".username@yahoo.com"),
            "username@yahoo.com.", "username@yahoo..com", "username@yahoo.c", "username@yahoo.corporate");

    @Test
    public void shallCreateEmailObjectIfAddressIsValid() {
        GOOD_EMAILS.forEach(Email::new);
    }

    @Test
    public void shallNotCreateEmailObjectIfAddressIsInvalid() {
        BAD_EMAILS.forEach(email -> {
            try {
                new Email(email);
                fail("Illegal argument exception expected for incorrect email: " + email);
            } catch (IllegalArgumentException e) {
            } catch (Exception e1) {
                fail("Illegal argument exception expected for incorrect email: " + email);
            }
        });
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