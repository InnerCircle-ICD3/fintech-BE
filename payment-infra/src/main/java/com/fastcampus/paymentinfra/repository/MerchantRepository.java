package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.MerchantReadOnly;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MerchantRepository {

    private final JdbcTemplate jdbcTemplate;

    public MerchantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsById(Long merchantId) {
        String sql = "SELECT COUNT(*) FROM merchant WHERE merchant_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, merchantId);
        return count != null && count > 0;
    }
}
