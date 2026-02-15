package com.demo.productservice.api;

import com.demo.productservice.config.UnstableConfig;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final UnstableConfig cfg;

  public AdminController(UnstableConfig cfg) {
    this.cfg = cfg;
  }

  @GetMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
  public ConfigResponse getConfig() {
    return new ConfigResponse(cfg.getFailPercent(), cfg.getDelayMs(), Instant.now());
  }

  @PostMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
  public ConfigResponse setConfig(
      @RequestParam(name = "failPercent", defaultValue = "0") int failPercent,
      @RequestParam(name = "delayMs", defaultValue = "0") int delayMs
  ) {
    cfg.setFailPercent(failPercent);
    cfg.setDelayMs(delayMs);
    return getConfig();
  }

  public record ConfigResponse(int failPercent, int delayMs, Instant updatedAt) {}
}
