package com.demo.clientservice.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(
    String id,
    String name,
    BigDecimal price,
    Instant generatedAt,
    String source,
    String note
) {}
