package com.buildsmart.iam.service;

import com.buildsmart.common.enums.Role;
import com.buildsmart.iam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to generate custom User IDs based on role-specific prefixes.
 * ID Format: {ROLE_PREFIX}{SEQUENCE_NUMBER}
 * Example: BSAD001, BSPM002, BSVM003
 */
@Service
@Transactional
public class UserIdGeneratorService {
    
    @Autowired
    private UserRepository userRepository;

    private static final int SEQUENCE_LENGTH = 3;
    
    /**
     * Generate a custom user ID based on the user's role.
     * 
     * @param role The role of the user
     * @return Generated user ID with format: {PREFIX}{SEQUENCE}
     */
    public String generateUserId(Role role) {
        String prefix = role.getIdPrefix();
        long nextSequence = getNextSequenceForRole(role);
        return String.format("%s%0" + SEQUENCE_LENGTH + "d", prefix, nextSequence);
    }
    
    /**
     * Get the next sequence number for a specific role prefix.
     * Counts existing users with the same role prefix and returns count + 1.
     * 
     * @param role The user's role
     * @return The next sequence number
     */
    private long getNextSequenceForRole(Role role) {
        long currentCount = userRepository.countByRole(role);
        return currentCount + 1;
    }
}
