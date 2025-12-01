package io.github.mm.jooq.product.internal;

import io.github.mm.jooq.product.generated.tables.records.ProductsRecord;
import io.github.mm.jooq.product.rest.dto.Product;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toProduct(ProductsRecord record) {
        if (record == null) {
            return null;
        }

        return new Product(
                record.getId(),
                record.getName(),
                record.getDescription(),
                record.getPrice(),
                record.getStockQuantity(),
                record.getSku(),
                record.getCategory(),
                record.getStatus() != null ? ProductStatus.valueOf(record.getStatus()) : null,
                record.getCreatedAt(),
                record.getUpdatedAt(),
                record.getCreatedBy(),
                record.getUpdatedBy());
    }
}
