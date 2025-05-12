package com.fastcampus.paymentapi.payment.utils;

import com.fastcampus.common.exception.BaseException;
import com.fastcampus.common.exception.PaymentErrorCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Hashtable;

public class QrUtils {

    public static String generateQrCodeBase64(String content, int width, int height) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                javax.imageio.ImageIO.write(image, "png", baos);
                byte[] bytes = baos.toByteArray();
                return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            throw new BaseException(PaymentErrorCode.QR_GENERATION_FAILED);
        }
    }
}
