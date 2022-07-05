package com.nevidelia.library.steganography;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.File;

public class Steganography {

    private int signatureLimit = 25;
    private final int bitMask = 0x00000001;

    public boolean setCharacterLimit(int limit) throws SignatureRangeException {
        int signatureLimitMax = 250;
        if (limit > 0 && limit <= signatureLimitMax) {
            signatureLimit = limit;
            return true;
        } else throw new SignatureRangeException("Make sure your signature length is within 1-" + signatureLimitMax + " characters.");
    }

    public Bitmap signPhotograph(File file, @NonNull String signature) throws SignatureRangeException {
        int signatureLength = signature.length();
        if (signatureLength > 0 && signatureLength <= this.signatureLimit) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            int bit, x = 0, y = 0;

            StringBuilder stringBuilder = new StringBuilder(signature);
            for (int foo = 0; foo < (this.signatureLimit - signatureLength); foo++)
                stringBuilder.append(" ");
            signature = stringBuilder.toString();

            for (int i = 0; i < signature.length(); i++) {
                bit = signature.charAt(i);
                for (int j = 0; j < 8; j++) {
                    int flag = bit & this.bitMask;
                    if (flag == 1) {
                        if (x < bitmap.getWidth()) {
                            bitmap.setPixel(x, y, bitmap.getColor(x, y).toArgb() | 0x00000001);
                            x++;
                        } else {
                            x = 0;
                            y++;
                            bitmap.setPixel(x, y, bitmap.getColor(x, y).toArgb() | 0x00000001);
                        }
                    } else {
                        if (x < bitmap.getWidth()) {
                            bitmap.setPixel(x, y, bitmap.getColor(x, y).toArgb() & 0xFFFFFFFE);
                            x++;
                        } else {
                            x = 0;
                            y++;
                            bitmap.setPixel(x, y, bitmap.getColor(x, y).toArgb() & 0xFFFFFFFE);
                        }
                    }
                    bit = bit >> 1;
                }
            }
            return bitmap;
        } else throw new SignatureRangeException("Make sure your signature length is within 1-" + this.signatureLimit + " characters.");
    }

    public String readSign(Bitmap bitmap) {
        StringBuilder stringBuilder = new StringBuilder();
        int flag, x = 0, y = 0;
        char[] c = new char[this.signatureLimit];

        for (int i = 0; i < this.signatureLimit; i++) {
            int bit = 0;
            for (int j = 0; j < 8; j++) {
                if (x < bitmap.getWidth()) {
                    flag = bitmap.getColor(x, y).toArgb() & this.bitMask;
                    x++;
                } else {
                    x = 0;
                    y++;
                    flag = bitmap.getColor(x, y).toArgb() & this.bitMask;
                }
                bit = bit >> 1;
                if (flag == 1) bit = bit | 0x80;
            }
            c[i] = (char) bit;
            stringBuilder.append(c[i]);
        }

        return stringBuilder.toString();
    }
}