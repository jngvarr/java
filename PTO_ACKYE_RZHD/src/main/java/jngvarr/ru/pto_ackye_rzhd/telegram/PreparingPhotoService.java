package jngvarr.ru.pto_ackye_rzhd.telegram;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Data
@Slf4j
@Component
public class PreparingPhotoService {
    /**
     * Метод декодирования штрихкода с изображения с помощью ZXing.
     *
     * @param image BufferedImage, считанное из файла.
     * @return текст штрихкода или null, если декодирование не удалось.
     */
    String decodeBarcode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            log.warn("Штрихкод не найден при первой попытке: {}", e.getMessage());
            // Попытка декодирования при повороте изображения
            for (int angle = 90; angle < 360; angle += 30) {
                BufferedImage rotated = rotateImage(image, angle);
                try {
                    LuminanceSource rotatedSource = new BufferedImageLuminanceSource(rotated);
                    BinaryBitmap rotatedBitmap = new BinaryBitmap(new HybridBinarizer(rotatedSource));
                    Result rotatedResult = new MultiFormatReader().decode(rotatedBitmap);
                    return rotatedResult.getText();
                } catch (NotFoundException ignored) {
                    // Продолжаем, если штрихкод не найден
                }
            }
            return null;
        }
    }

    /**
     * Метод разворота изображения с помощью.
     *
     * @param src   BufferedImage, считанное из файла,
     * @param angle int, угол поворота изображения
     * @return BufferedImage повернутое на заданный угол исходное изображение
     */
    private BufferedImage rotateImage(BufferedImage src, int angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));
        int w = src.getWidth();
        int h = src.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, src.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.translate((newWidth - w) / 2, (newHeight - h) / 2);
        g2d.rotate(rads, w / 2, h / 2);
        g2d.drawRenderedImage(src, null);
        g2d.dispose();
        return rotated;
    }
    BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }

    // Преобразование цветного изображение в оттенки серого
    BufferedImage convertToGrayscale(BufferedImage src) {
        BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = gray.getGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return gray;
    }

}
