package com.demo.clientservice.api;

import com.demo.clientservice.model.ProductDto;
import com.demo.clientservice.service.CatalogService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

  private final CatalogService catalog;

  public CatalogController(CatalogService catalog) {
    this.catalog = catalog;
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ProductDto get(@PathVariable String id) {
    return catalog.getProduct(id);
  }

  @GetMapping(value = "/batch/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ProductDto> batch(
      @PathVariable String id,
      @RequestParam(name = "n", defaultValue = "10") int n
  ) {
    int safeN = Math.max(1, Math.min(200, n));
    List<ProductDto> out = new ArrayList<>(safeN);
    for (int i = 0; i < safeN; i++) {
      out.add(catalog.getProduct(id));
    }
    return out;
  }
}
