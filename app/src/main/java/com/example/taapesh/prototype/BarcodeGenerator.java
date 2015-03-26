package com.example.taapesh.prototype;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public class BarcodeGenerator {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static final int BARCODE_WIDTH = 600;
    private static final int BARCODE_HEIGHT = 300;

    public Bitmap encodeAsBitmap(String code, BarcodeFormat format) throws WriterException {
        String contentsToEncode = code;
        if (contentsToEncode == null) {
            return null;
        }

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);

        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    // Get format of barcode read by Scandit
    public BarcodeFormat getBarcodeFormat(String symbology) {
        BarcodeFormat format = null;

        switch (symbology) {
            case "UPC12":
                format = BarcodeFormat.UPC_A;
                break;
            case "CODE128":
                format = BarcodeFormat.CODE_128;
                break;
            case "EAN8":
                format = BarcodeFormat.EAN_8;
                break;
            case "EAN13":
                format = BarcodeFormat.EAN_13;
                break;
            case "UPCE":
                format = BarcodeFormat.UPC_E;
                break;
            case "GS1-128":
                break;
        }
        return format;
    }
}
