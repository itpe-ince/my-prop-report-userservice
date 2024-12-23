package com.dnc.mprs.userservice.repository.rowmapper;

import com.dnc.mprs.userservice.domain.UserInfo;
import com.dnc.mprs.userservice.domain.enumeration.GenderType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserInfo}, with proper type conversions.
 */
@Service
public class UserInfoRowMapper implements BiFunction<Row, String, UserInfo> {

    private final ColumnConverter converter;

    public UserInfoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserInfo} stored in the database.
     */
    @Override
    public UserInfo apply(Row row, String prefix) {
        UserInfo entity = new UserInfo();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", String.class));
        entity.setFirstname(converter.fromRow(row, prefix + "_firstname", String.class));
        entity.setLastname(converter.fromRow(row, prefix + "_lastname", String.class));
        entity.setAlias(converter.fromRow(row, prefix + "_alias", String.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", GenderType.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setAddressLine1(converter.fromRow(row, prefix + "_address_line_1", String.class));
        entity.setAddressLine2(converter.fromRow(row, prefix + "_address_line_2", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        return entity;
    }
}
