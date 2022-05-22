package mykotlinpackage.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "OrderInfo", description = "A model acting as a subset of the Order entity")
class OrderInfo(
        var email: String? = null,
        var comment: String? = null,
)