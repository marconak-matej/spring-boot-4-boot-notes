package io.github.mm.flyway.product.repository;

import io.github.mm.flyway.product.domain.Product;
import io.github.mm.flyway.product.domain.ProductCategory;
import io.github.mm.flyway.product.domain.ProductStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
        extends PagingAndSortingRepository<@NonNull Product, @NonNull Long>,
                CrudRepository<@NonNull Product, @NonNull Long> {

    Page<@NonNull Product> findByStatus(Pageable pageable, @Param("status") ProductStatus status);

    Page<@NonNull Product> findByCategory(Pageable pageable, @Param("category") ProductCategory category);
}
