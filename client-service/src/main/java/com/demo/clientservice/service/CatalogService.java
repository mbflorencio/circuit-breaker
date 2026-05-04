package com.demo.clientservice.service;

import com.demo.clientservice.client.ProductApiClient;
import com.demo.clientservice.model.ProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

  private final ProductApiClient productApi;

  public CatalogService(ProductApiClient productApi) {
    this.productApi = productApi;
  }

  @CircuitBreaker(name = "productCB", fallbackMethod = "fallbackProduct")
  public ProductDto getProduct(String id) {
    var p = productApi.fetchProduct(id);
    return new ProductDto(p.id(), p.name(), p.price(), p.generatedAt(), "product-service", null);
  }

  @SuppressWarnings("unused")
  private ProductDto fallbackProduct(String id, Throwable t) {
    String note = (t == null)
        ? "fallback"
        : t.getClass().getSimpleName() + ": " + safeMessage(t);

    return new ProductDto(
        id,
        "Fallback Product " + id,
        BigDecimal.ZERO,
        Instant.now(),
        "fallback",
        note
    );
  }

  private static String safeMessage(Throwable t) {
    String m = t.getMessage();
    if (m == null) return "";
    return m.length() > 120 ? m.substring(0, 120) + "..." : m;
  }
}
