package mykotlinpackage.dto

import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel

class OrderUpdateEmailDTO : AbstractSystemUuidPersistableModel() {
    val email: String? = null
}