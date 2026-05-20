package com.example.bookstore.service;

import com.example.bookstore.repository.InvalidatedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupService {

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    // Chạy vào lúc 00:00:00 (nửa đêm) mỗi ngày
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Bắt đầu dọn dẹp các Token rác đã hết hạn...");
        invalidatedTokenRepository.deleteAllExpiredSince(new Date());
        log.info("Dọn dẹp Token hoàn tất!");
    }
}
