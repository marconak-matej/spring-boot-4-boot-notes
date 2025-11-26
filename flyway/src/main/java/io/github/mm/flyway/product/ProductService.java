package io.github.mm.flyway.product;

import io.github.mm.flyway.infrastructure.SystemClock;
import io.github.mm.flyway.infrastructure.audit.AuditProvider;
import io.github.mm.flyway.infrastructure.exception.NotFoundException;
import io.github.mm.flyway.product.domain.Product;
import io.github.mm.flyway.product.repository.ProductRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;
    private final SystemClock clock;
    private final AuditProvider auditProvider;

    public ProductService(ProductRepository repository, SystemClock clock, AuditProvider auditProvider) {
        this.repository = repository;
        this.clock = clock;
        this.auditProvider = auditProvider;
    }

    public Page<@NonNull Product> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product create(Product product) {
        var productToSave = product.withAuditFields(clock.now(), auditProvider.user());
        return repository.save(productToSave);
    }

    @Transactional
    public Product update(Long id, Product product) {
        var existing = findById(id);
        var updatedProduct = existing.withUpdatedFields(
                product.name(),
                product.description(),
                product.price(),
                product.stockQuantity(),
                product.sku(),
                product.category(),
                product.status(),
                clock.now(),
                auditProvider.user());
        return repository.save(updatedProduct);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
