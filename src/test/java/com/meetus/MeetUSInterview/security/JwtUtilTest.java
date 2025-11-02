package com.meetus.MeetUSInterview.security;

import com.meetus.MeetUSInterview.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for JwtUtil
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", 
            "mySecretKeyForJWTTokenGenerationAndValidationPleaseChangeInProductionEnvironment1234567890");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 86400000L);

        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("hashedPassword")
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(testUser);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void testExtractUserId() {
        String token = jwtUtil.generateToken(testUser);

        String userId = jwtUtil.extractUserId(token);

        assertThat(userId).isEqualTo("1");
    }

    @Test
    void testIsTokenValid() {
        String token = jwtUtil.generateToken(testUser);

        boolean isValid = jwtUtil.isTokenValid(token, testUser);

        assertThat(isValid).isTrue();
    }

    @Test
    void testIsTokenInvalid_WrongUser() {
        String token = jwtUtil.generateToken(testUser);

        User differentUser = User.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .password("hashedPassword")
                .build();

        boolean isValid = jwtUtil.isTokenValid(token, differentUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void testGetExpirationTime() {
        long expirationTime = jwtUtil.getExpirationTime();

        assertThat(expirationTime).isEqualTo(86400000L);
    }
}
