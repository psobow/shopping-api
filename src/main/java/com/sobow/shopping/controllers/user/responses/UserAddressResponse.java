package com.sobow.shopping.controllers.user.responses;

import lombok.Builder;

@Builder
public record UserAddressResponse(
    String cityName,
    String streetName,
    String streetNumber,
    String postCode
) {

}
