package com.demo.productservice.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Product(
    String id,
    String name,
    BigDecimal price,
    Instant generatedAt
) {}
