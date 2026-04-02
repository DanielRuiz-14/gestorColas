package com.queuetable.restaurant.domain;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QrCodeService {

    private final String baseUrl;
    private final int width;
    private final int height;

    public QrCodeService(@Value("${app.qr.base-url}") String baseUrl,
                         @Value("${app.qr.width}") int width,
                         @Value("${app.qr.height}") int height) {
        this.baseUrl = baseUrl;
        this.width = width;
        this.height = height;
    }

    public byte[] generateQrCode(String slug) {
        String url = baseUrl + "/" + slug;
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public String buildQrEndpointUrl(String restaurantId) {
        return "/restaurants/" + restaurantId + "/qr";
    }
}
