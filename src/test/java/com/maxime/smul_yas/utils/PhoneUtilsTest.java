package com.maxime.smul_yas.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneUtilsTest {

    @Test
    void testNormalizePhone() {
        assertNull(PhoneUtils.normalizePhone(null));
        assertEquals("+22896118586", PhoneUtils.normalizePhone(" 22896118586"));
        assertEquals("+22896118586", PhoneUtils.normalizePhone("+22896118586"));
        assertEquals("+22896118586", PhoneUtils.normalizePhone(" 228 96 11 85 86"));
        assertEquals("+22896118586", PhoneUtils.normalizePhone("+228 96 11 85 86"));
        assertEquals("22896118586", PhoneUtils.normalizePhone("22896118586"));
        assertEquals("22896118586", PhoneUtils.normalizePhone("228 96 11 85 86 "));
    }
}
