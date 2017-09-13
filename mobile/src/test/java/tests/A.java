package tests;

import org.junit.Test;

public class A {

    @Test
    public void testVersion() {
        System.out.println(getVersionString(0x233E));
        System.out.println(getVersionString(0xF99F));
        System.out.println(getVersionValue("0x2099F"));
        System.out.println(getVersionValue("0xF99F"));
    }

    public int getVersionValue(String str) {
        String ver = str.trim().replace(".0", "").replace(".", "");
        int v;
        if (ver.startsWith("0x") || ver.startsWith("0X")) {
            ver = ver.substring(2);
        }
        try {
            v = Integer.parseInt(ver, 16);
        } catch (Exception e) {
            return -1;
        }
        return v;
    }

    public String getVersionString(int value) {
        int last = (value & 0xf);
        int m = ((value >> 4) & 0xff);
        int b = ((value >> 12) & 0xf);
        return String.format("%X.%03X.%X", b, m, last);
    }
}