package at.mci.bugtracker.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {
    @BeforeAll
    static void setUp() throws Exception {
        System.out.println("Testing PasswordHasher.java");
    }

    @Test
    void hash_producesBcryptFormat() {
        String hash = PasswordHasher.hash("secret123");
        assertThat(hash).startsWith("$2a$10$");
    }

    @Test
    void hash_sameInputProducesDifferentHashes() {
        String h1 = PasswordHasher.hash("secret123");
        String h2 = PasswordHasher.hash("secret123");
        assertThat(h1).isNotEqualTo(h2);
    }

    @Test
    void verify_correctPasswordReturnsTrue() {
        String hash = PasswordHasher.hash("secret123");
        assertThat(PasswordHasher.verify("secret123", hash)).isTrue();
    }

    @Test
    void verify_wrongPasswordReturnsFalse() {
        String hash = PasswordHasher.hash("secret123");
        assertThat(PasswordHasher.verify("falschesPasswort", hash)).isFalse();
    }

    @Test
    void verify_nullPlainReturnsFalse() {
        String hash = PasswordHasher.hash("secret123");
        assertThat(PasswordHasher.verify(null, hash)).isFalse();
    }

    @Test
    void verify_nullHashReturnsFalse() {
        assertThat(PasswordHasher.verify("secret123", null)).isFalse();
    }

    @Test
    void verify_bothNullReturnsFalse() {
        assertThat(PasswordHasher.verify(null, null)).isFalse();
    }
}
