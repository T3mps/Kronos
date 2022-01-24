package net.acidfrog.kronos.core.lang;

import java.util.UUID;

public final class IDArbiter {

    private final static char[] CHARSET = {
        '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l',
        'm','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','-','.','_','~'
    };

    private IDArbiter() { }

    public static String next() {
        UUID u = UUID.randomUUID();
        return toIDString(u.getMostSignificantBits()) + toIDString(u.getLeastSignificantBits());
    }

    private static String toIDString(long i) {
        char[] result = new char[32];
        int offset = 1 << 5;
        int pointer = offset;
        long b = (1 << 6) - 1; // 63 0b00111111
        
        do {
            result[--pointer] = CHARSET[(int) (i & b)]; // 0 - 63 | CHARSET.length
            i >>>= 6;
        } while (i != 0);
        
        return new String(result, pointer, (offset - pointer));
    }
    
}
