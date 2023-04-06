package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass

@ScrudBean
@Entity
@IdClass(CustomerId::class)
class Customer(
    @field:Id
    var name: String? = null,

    @field:Id
    var phoneNumber: String? = null,

    var address: String? = null
)
