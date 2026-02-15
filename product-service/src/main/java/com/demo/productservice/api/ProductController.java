package com.demo.productservice.api;

import com.demo.productservice.config.UnstableConfig;
import com.demo.productservice.model.Product;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping
public class ProductController {

  private final UnstableConfig cfg;

  public ProductController(UnstableConfig cfg) {
    this.cfg = cfg;
  }

  @GetMapping(value = "/products/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Product getProduct(@PathVariable String id) throws InterruptedException {
    int delay = cfg.getDelayMs();
    if (delay > 0) {
      Thread.sleep(delay);
    }

    int fail = cfg.getFailPercent();
    if (fail > 0 && ThreadLocalRandom.current().nextInt(100) < fail) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "boom (simulated)");
    }

    long cents = ThreadLocalRandom.current().nextLong(500, 25000); // 5.00 .. 250.00
    BigDecimal price = BigDecimal.valueOf(cents, 2);

    return new Product(id, "Product " + id, price, Instant.now());
  }
}
