package mykotlinpackage.dto

import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel

class OrderUpdateCommentDTO : AbstractSystemUuidPersistableModel() {
    val comment: String? = null
}