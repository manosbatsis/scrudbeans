package mykotlinpackage.model

import jakarta.persistence.IdClass
import java.io.Serializable
import jakarta.persistence.MappedSuperclass

/** Used as [IdClass] for the [Customer] entity */
@MappedSuperclass
data class CustomerId(
    val name: String,
    val phoneNumber: String
) : Serializable
