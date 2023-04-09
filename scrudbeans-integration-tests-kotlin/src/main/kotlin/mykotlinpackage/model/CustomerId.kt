package mykotlinpackage.model

import jakarta.persistence.IdClass
import jakarta.persistence.MappedSuperclass
import java.io.Serializable

/** Used as [IdClass] for the [Customer] entity */
@MappedSuperclass
data class CustomerId(
    val name: String,
    val phoneNumber: String,
) : Serializable
