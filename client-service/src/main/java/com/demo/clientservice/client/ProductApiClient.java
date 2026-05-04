package com.demo.clientservice.client;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductApiClient {

  private final RestClient restClient;

  public ProductApiClient(RestClient.Builder builder,
                          @Value("${product.base-url}") String baseUrl) {
    this.restClient = builder.baseUrl(baseUrl).build();
  }

  public DownstreamProduct fetchProduct(String id) {
    return restClient.get()
        .uri("/products/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(DownstreamProduct.class);
  }

  public record DownstreamProduct(
      String id,
      String name,
      BigDecimal price,
      Instant generatedAt
  ) {}
}
