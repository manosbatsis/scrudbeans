package mykotlinpackage.model

import java.io.Serializable

data class CustomerId(
    val name: String?,
    val phoneNumber: String
): Serializable