package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

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