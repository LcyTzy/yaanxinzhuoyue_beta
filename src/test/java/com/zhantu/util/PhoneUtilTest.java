package com.zhantu.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneUtilTest {

    @Test
    void testValidPhone() {
        assertTrue(PhoneUtil.isValidPhone("13800138000"));
        assertTrue(PhoneUtil.isValidPhone("15912345678"));
        assertTrue(PhoneUtil.isValidPhone("18612345678"));
    }

    @Test
    void testInvalidPhone() {
        assertFalse(PhoneUtil.isValidPhone("12345678901"));
        assertFalse(PhoneUtil.isValidPhone("1380013800"));
        assertFalse(PhoneUtil.isValidPhone("23800138000"));
        assertFalse(PhoneUtil.isValidPhone(null));
        assertFalse(PhoneUtil.isValidPhone(""));
    }

    @Test
    void testMaskPhone() {
        assertEquals("138****8000", PhoneUtil.maskPhone("13800138000"));
        assertEquals("159****5678", PhoneUtil.maskPhone("15912345678"));
    }

    @Test
    void testMaskPhoneInvalid() {
        assertEquals("123", PhoneUtil.maskPhone("123"));
        assertNull(PhoneUtil.maskPhone(null));
    }
}
