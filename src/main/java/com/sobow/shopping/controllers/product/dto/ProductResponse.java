package com.sobow.shopping.controllers.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

/**
 * <p>IDs of images associated with product response.</p>
 *
 * {@link JsonInclude}(Include.NON_NULL) is applied so the field is omitted from JSON when no image IDs are provided.
 *
 * There are two mapping paths:
 * <ul>
 *   <li>mapToDto(Product) – skips fetching image IDs to avoid
 *       triggering lazy relations / BLOB loads. In this path, the value is {@code null}. Used in update endpoint.</li>
 *   <li>mapToDto(Product, List&lt;Long&gt;) – used when IDs are
 *       pre-fetched via a lightweight query.</li>
 * </ul>
 * </br>
 * Omitting the property when {@code null}:
 * <ul>
 *   <li>Keeps the payload clean (no {@code "imageIds": null}).</li>
 *   <li>Clearly distinguishes “not provided” vs. “empty list”.</li>
 *   <li>Avoids confusing clients and prevents accidental lazy loading.</li>
 * </ul>
 *
 */
@Builder
public record ProductResponse(
    Long id,
    String name,
    String brandName,
    BigDecimal price,
    Integer availableQty,
    String description,
    Long categoryId,
    @JsonInclude(Include.NON_NULL) List<Long> imageIds
) {
    
}
