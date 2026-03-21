package com.buildsmart.common.util;

import org.springframework.stereotype.Component;

/**
 * Utility for generating custom formatted IDs across the application.
 * Format: PREFIX + SEQUENCE (3 digits)
 */
@Component
public class IdGeneratorUtil {

    private static final int SEQUENCE_LENGTH = 3;

    /**
     * Generates the next ID in format PREFIX + zero-padded sequence.
     *
     * @param prefix   The prefix (e.g., CHEBS26, BUDBS, FINBS)
     * @param nextSeq  The next sequence number
     * @return Generated ID (e.g., CHEBS26001, BUDBS001)
     */
    public String generateId(String prefix, long nextSeq) {
        return String.format("%s%0" + SEQUENCE_LENGTH + "d", prefix, nextSeq);
    }
}
