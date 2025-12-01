package io.github.mm.jooq.product.repository;

import static io.github.mm.jooq.product.generated.tables.Products.PRODUCTS;

import io.github.mm.jooq.product.generated.tables.records.ProductsRecord;
import io.github.mm.jooq.product.rest.dto.CreateProductRequest;
import io.github.mm.jooq.product.rest.dto.ProductStatus;
import io.github.mm.jooq.product.rest.dto.UpdateProductRequest;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {

    private final DSLContext dsl;

    public ProductRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public ProductsRecord create(CreateProductRequest request, String createdBy) {
        var id = Objects.requireNonNull(dsl.insertInto(PRODUCTS)
                        .set(PRODUCTS.NAME, request.name())
                        .set(PRODUCTS.DESCRIPTION, request.description())
                        .set(PRODUCTS.PRICE, request.price())
                        .set(PRODUCTS.STOCK_QUANTITY, request.stockQuantity())
                        .set(PRODUCTS.SKU, request.sku())
                        .set(PRODUCTS.CATEGORY, request.category())
                        .set(
                                PRODUCTS.STATUS,
                                request.status() != null ? request.status().name() : ProductStatus.ACTIVE.name())
                        .set(PRODUCTS.CREATED_AT, LocalDateTime.now())
                        .set(PRODUCTS.CREATED_BY, createdBy)
                        .returningResult(PRODUCTS.ID)
                        .fetchOne())
                .value1();

        return findById(id).orElse(null);
    }

    public Optional<ProductsRecord> findById(Long id) {
        return Optional.ofNullable(
                dsl.selectFrom(PRODUCTS).where(PRODUCTS.ID.eq(id)).fetchOne());
    }

    public Page<@NonNull ProductsRecord> findAll(Pageable pageable, ProductStatus status, String category) {
        Condition condition = DSL.trueCondition();

        if (status != null) {
            condition = condition.and(PRODUCTS.STATUS.eq(status.name()));
        }

        if (category != null && !category.isBlank()) {
            condition = condition.and(PRODUCTS.CATEGORY.equalIgnoreCase(category));
        }

        var total = dsl.selectCount().from(PRODUCTS).where(condition).fetchOne(0, Long.class);

        var content = dsl.selectFrom(PRODUCTS)
                .where(condition)
                .orderBy(PRODUCTS.ID)
                .limit(pageable.getPageSize())
                .offset((int) pageable.getOffset())
                .fetch();
        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    public Optional<ProductsRecord> update(Long id, UpdateProductRequest request, String updatedBy) {
        int updated = dsl.update(PRODUCTS)
                .set(PRODUCTS.NAME, request.name())
                .set(PRODUCTS.DESCRIPTION, request.description())
                .set(PRODUCTS.PRICE, request.price())
                .set(PRODUCTS.STOCK_QUANTITY, request.stockQuantity())
                .set(PRODUCTS.SKU, request.sku())
                .set(PRODUCTS.CATEGORY, request.category())
                .set(
                        PRODUCTS.STATUS,
                        request.status() != null ? request.status().name() : null)
                .set(PRODUCTS.UPDATED_AT, LocalDateTime.now())
                .set(PRODUCTS.UPDATED_BY, updatedBy)
                .where(PRODUCTS.ID.eq(id))
                .execute();

        return updated > 0 ? findById(id) : Optional.empty();
    }

    public boolean deleteById(Long id) {
        int deleted = dsl.deleteFrom(PRODUCTS).where(PRODUCTS.ID.eq(id)).execute();
        return deleted > 0;
    }
}
