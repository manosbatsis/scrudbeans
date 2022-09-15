package mykotlinpackage.model

import javax.persistence.IdClass
import java.io.Serializable
import javax.persistence.MappedSuperclass

/** Used as [IdClass] for the [Customer] entity */
@MappedSuperclass
data class CustomerId(
    val name: String,
    val phoneNumber: String
) : Serializable
