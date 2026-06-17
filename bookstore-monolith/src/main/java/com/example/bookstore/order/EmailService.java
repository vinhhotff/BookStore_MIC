package com.example.bookstore.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Async // Phép thuật: Chạy ngầm ở một luồng (Thread) khác
    public void sendOrderConfirmation(String emailTo) {
        log.info("Bắt đầu gửi Email xác nhận cho user: " + emailTo);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUsername != null && !mailUsername.isEmpty() ? mailUsername : "your-email@gmail.com");
            message.setTo(emailTo);
            message.setSubject("Xác nhận Đơn hàng Thành Công - Bookstore");
            message.setText("Cảm ơn bạn đã đặt mua sách tại hệ thống của chúng tôi.\nĐơn hàng của bạn đã được ghi nhận và đang được xử lý.");
            
            mailSender.send(message);
            
            log.info("Đã gửi Email thành công cho: " + emailTo + "!");
        } catch (Exception e) {
            log.error("Lỗi khi gửi email", e);
        }
    }
}
