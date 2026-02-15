package com.demo.productservice.config;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class UnstableConfig {
  private final AtomicInteger failPercent = new AtomicInteger(0);
  private final AtomicInteger delayMs = new AtomicInteger(0);

  public int getFailPercent() {
    return failPercent.get();
  }

  public int getDelayMs() {
    return delayMs.get();
  }

  public void setFailPercent(int value) {
    failPercent.set(clamp(value, 0, 100));
  }

  public void setDelayMs(int value) {
    delayMs.set(Math.max(0, value));
  }

  private static int clamp(int v, int min, int max) {
    return Math.max(min, Math.min(max, v));
  }
}
