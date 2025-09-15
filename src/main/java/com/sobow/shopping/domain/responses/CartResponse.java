package com.sobow.shopping.domain.responses;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Long id, List<Long> itemsId, BigDecimal totalPrice) {

}
