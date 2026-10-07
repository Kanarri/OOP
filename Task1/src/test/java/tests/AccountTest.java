package tests;

import model.Account;
import model.Housing;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Housing housing() {
        return new Housing("ул. Ленина, 1", 5_000, 3);
    }

    @Test
    void equals_sameNumber_true() {
        Account a = new Account("ЛС-001", housing());
        Account b = new Account("ЛС-001", new Housing("другой адрес", 1_000, 1));
        assertEquals(a, b);
    }

    @Test
    void equals_differentNumber_false() {
        Account a = new Account("ЛС-001", housing());
        Account b = new Account("ЛС-002", housing());
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_sameNumber_equal() {
        Account a = new Account("ЛС-001", housing());
        Account b = new Account("ЛС-001", new Housing("другой адрес", 1_000, 1));
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void constructor_blankNumber_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account("   ", housing()));
    }
}