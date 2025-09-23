package com.sobow.shopping.domain.user.responses;

import lombok.Builder;

@Builder
public record UserAddressResponse(
    String cityName,
    String streetName,
    String streetNumber,
    String postCode
) {

}
