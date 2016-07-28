package eu.socialedge.hermes.domain.v2.operator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

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
    public void testGoodEmails() {
        GOOD_EMAILS.forEach(Email::new);
    }

    @Test
    public void testBadEmails() {
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
    public void testGetRawMessage() {
        GOOD_EMAILS.forEach(emailAddress -> {
            assertEquals(emailAddress, new Email(emailAddress).address());
        });
    }

    @Test
    public void testGetDomainAndMailboxNames() {
        final String emailAddress = "user_name@domain.com";
        final String emailAddressMailboxName = "user_name";
        final String emailAddressDomainName = "domain.com";

        Email email = new Email(emailAddress);

        assertEquals(emailAddressMailboxName, email.mailboxName());
        assertEquals(emailAddressDomainName, email.domainName());
    }
}