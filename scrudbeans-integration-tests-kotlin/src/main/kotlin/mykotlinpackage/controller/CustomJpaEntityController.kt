package mykotlinpackage.controller

import com.github.manosbatsis.kotlin.utils.api.Dto
import com.github.manosbatsis.scrudbeans.controller.AbstractJpaEntityController
import com.github.manosbatsis.scrudbeans.service.JpaEntityService

open class CustomJpaEntityController<
    T : Any, S : Any, SRV : JpaEntityService<T, S>, DTO : Dto<T>
    > : AbstractJpaEntityController<T, S, SRV, DTO>()
