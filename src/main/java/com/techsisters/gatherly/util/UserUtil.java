package com.techsisters.gatherly.util;

import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    // Create a single, reusable instance of SecureRandom
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static final String ANONYMOUS = "anonymous";

    /**
     * Gets the username (email) of the currently authenticated user.
     * Used primarily for audit logging.
     *
     * @return Email if authenticated, "annonymous" if not
     */
    public static String getLoginUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return ANONYMOUS;
        } else {
            return auth.getName();
        }
    }

    public static boolean getAnonymous() {
        return StringUtils.equals(ANONYMOUS, getLoginUsername());
    }

    /**
     * Generates a 6-digit (000000-999999) verification code.
     * 
     * @return A 6-digit string code.
     */
    public static int generate6DigitCode() {

        // Generates a number between 100,000 and 999,999
        return 100_000 + SECURE_RANDOM.nextInt(900_000);
    }

}
