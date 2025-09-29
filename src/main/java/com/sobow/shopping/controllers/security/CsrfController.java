package com.sobow.shopping.controllers.security;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}")
@Tag(
    name = "CSRF Controller - Disabled in Security Config class",
    description = "API to generate CSRF token"
)
public class CsrfController {

//    @Operation(
//        summary = "Get CSRF token"
//    )
//    @ApiResponses({
//        @ApiResponse(responseCode = "200", description = "CSRF token generated")
//    })
//    @GetMapping("/csrf")
//    public CsrfToken csrf(CsrfToken token) {
//        return token;
//    }
}
