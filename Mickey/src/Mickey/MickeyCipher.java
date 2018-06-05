package Mickey;
 
public class MickeyCipher {
    
public final int REGISTER_LENGTH = 100; 
    
public class Context {
    public int [] r = new int [100];
    public int [] s = new int [100];
    public int [] key = new int [80];
}
 
public Context context;
    
public final int [] rMask = {
        1,1,0,1,1,1,1,0,0,1,0,0,1,1,0,0,1,0,0,1,1,1,1,0,0,
        1,0,0,1,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0,1,1,0,0,0,
        1,0,1,0,1,0,1,0,1,0,1,1,0,1,1,1,1,1,0,0,0,1,1,0,0,
        0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,1,1,0,1,1,1,1,0,0};
    
public final int [] comp0 = {
        0,0,0,0,1,1,0,0,0,1,0,1,1,1,1,0,1,0,0,1,0,1,0,1,0,
        1,0,1,0,1,1,0,1,0,0,1,0,0,0,0,0,0,0,1,0,1,0,1,0,1,
        0,0,0,0,1,0,1,0,0,1,1,1,1,0,0,1,0,1,0,1,1,1,1,1,1,
        1,1,1,0,1,0,1,1,1,1,1,1,0,1,0,1,0,0,0,0,0,0,1,1};
 
public final int [] comp1 = {
        0,1,0,1,1,0,0,1,0,1,1,1,1,0,0,1,0,1,0,0,0,1,1,0,1,
        0,1,1,1,0,1,1,1,1,0,0,0,1,1,0,1,0,1,1,1,0,0,0,0,1,
        0,0,0,1,0,1,1,1,0,0,0,1,1,1,1,1,1,0,1,0,1,1,1,0,1,
        1,1,1,0,0,0,1,0,0,0,0,1,1,1,0,0,0,1,0,0,1,1,0,0};
 
public final int [] fb0 = {
        1,1,1,1,0,1,0,1,1,1,1,1,1,1,1,0,0,1,0,1,1,1,1,1,1,
        1,1,1,1,0,0,1,1,0,0,0,0,0,0,1,1,1,0,0,1,0,0,1,0,1,
        0,1,0,0,1,0,1,1,1,1,0,1,0,1,0,1,0,0,0,0,0,0,0,0,0,
        1,1,0,1,0,0,0,1,1,0,1,1,1,0,0,1,1,1,0,0,1,1,0,0,0};
 
public final int [] fb1 = {
        1,1,1,0,1,1,1,0,0,0,0,1,1,1,0,1,0,0,1,1,0,0,0,1,0,
        0,1,1,0,0,1,0,1,1,0,0,0,1,1,0,0,0,0,0,1,1,0,1,1,0,
        0,0,1,0,0,0,1,0,0,1,0,0,1,0,1,1,0,1,0,1,0,0,1,0,1,
        0,0,0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0,0,1,0,0,0,0,1};
 
private void clockR(
        int inputBitR,
        int controlBitR) {
    int feedbackBit = context.r[99] ^ inputBitR;
    
    if (controlBitR == 1) {
        if (feedbackBit == 1) {
            for (int i = 99; i > 0; --i) {
                context.r[i] = context.r[i - 1] ^ context.r[i] ^ rMask[i];
            }
            context.r[0] = rMask[0] ^ context.r[0];
        } else {
            for (int i = 99; i > 0; --i) {
                context.r[i] = context.r[i - 1] ^ context.r[i];
            }
        }
    } else {
        if (feedbackBit == 1) {
            for (int i = 99; i > 0; --i) {
                context.r[i] = context.r[i - 1] ^ rMask[i];
            }
            context.r[0] = rMask[0];
        } else {
            for (int i = 99; i > 0; --i) {
                context.r[i] = context.r[i - 1];
            }   
            context.r[0] = 0;
        }
    }
}
 
private void clockS(
        int inputBitS,
        int controlBitS) {
    int [] sHat = new int [100];
    int feedbackBit = context.s[99] ^ inputBitS;
    
    for (int i = 98; i > 0; --i) {
        sHat[i] = context.s[i - 1] ^ ((context.s[i] ^ comp0[i]) & (context.s[i + 1] ^ comp1[i]));
    }
    sHat[0] = 0;
    sHat[99] = context.s[98];
    
    for (int i = 0; i < 100; ++i) {
        context.s[i] = sHat[i];
    }
    
    if (feedbackBit == 1) {
        if (controlBitS == 1) {
            for (int i = 0; i < 100; ++i) {
                context.s[i] = sHat[i] ^ fb1[i];
            }   
        } else {
            for (int i = 0; i < 100; ++i) {
                context.s[i] = sHat[i] ^ fb0[i];
            }
        }
    }
}
 
private int clockKG(boolean mixing, int inputBit) {
    int keystreamBit = (context.r[0] ^ context.s[0]) & 1;
    int controlBitR = context.s[34] ^ context.r[67];
    int controlBitS = context.s[67] ^ context.r[33];
    
    if (mixing) {
        clockR(inputBit ^ context.s[50], controlBitR);
    } else {
        clockR(inputBit, controlBitR);
    }
    
    clockS(inputBit, controlBitS);
    
    return keystreamBit;
}
 
public MickeyCipher(int [] key, int [] iv) {
    context = new Context();
    context.key = key;
    
    for (int i = 0; i < 100; ++i) {
        context.r[i] = 0;
        context.s[i] = 0;
    }
    
    for (int i = 0; i < iv.length * 8; ++i) {
        clockKG(true, (iv[i / 8] >> (7 - (i % 8))) & 1);
    }
    
    for (int i = 0; i < 80; ++i) {
        clockKG(true, (key[i / 8] >> (7 - (i % 8))) & 1);
    }
    
    for (int i = 0; i < 100; ++i) {
        clockKG(true, 0);
    }
}
 
private int [] keystream (int length) {
    int [] keystream = new int [length];
    
    for (int i = 0; i < length; ++i) {
        keystream[i] = 0;
        for (int j = 0; j < 8; ++j) {
            keystream[i] ^= clockKG(false, 0) << (7 - j);
        }
    }
    
    return keystream;
}
 
public int [] encrypt(int [] bytes) {
    int [] output = new int [bytes.length];
    for (int i = 0; i < bytes.length; ++i) {
        output[i] = bytes[i];
        for (int j = 0; j < 8; ++j) {
            output[i] ^= clockKG(false, 0) << (7 - j);
        }
    }
    return output;
}
 
private static int [] toBitArray(String hex) {
    int [] bin = new int [hex.length() / 2];
    for (int i = 0; i < hex.length(); i += 2) {
        bin[i / 2] = Integer.parseInt("" + hex.substring(i, i + 2), 16);
    }
    return bin;
}
 
public static void main(String[] args) {
    String key = "3b80fc8c475fc270fa26";
    String iv = "";
    
    MickeyCipher cipher = new MickeyCipher(toBitArray(key), toBitArray(iv));
    
    int [] stream = cipher.keystream(16);
    
    String out = "";
    for (int i = 0; i < stream.length; ++i) {
        out += Integer.toHexString(stream[i]) + " ";
    }
    
    System.out.println(out);
}
 
}