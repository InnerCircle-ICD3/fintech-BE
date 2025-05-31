package com.fastcampus.paymentinfra.repository;

import com.fastcampus.paymentinfra.entity.KeysReadOnly;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class KeysRepository {

    private final JdbcTemplate jdbcTemplate;

    public KeysRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<KeysReadOnly> findByMerchantId(Long merchantId) {
        String sql = "SELECT keys_id, encrypted_key, merchant_id, created_at, updated_at " +
                "FROM keys WHERE merchant_id = ?";
        return jdbcTemplate.query(sql, ps -> ps.setLong(1, merchantId), rs -> {
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        });
    }

    private KeysReadOnly mapRow(ResultSet rs) throws SQLException {
        return new KeysReadOnly(
                rs.getLong("keys_id"),
                rs.getString("encrypted_key"),
                rs.getLong("merchant_id"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
