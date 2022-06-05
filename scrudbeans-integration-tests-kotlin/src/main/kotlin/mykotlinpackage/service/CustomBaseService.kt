package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.JpaEntityService

interface CustomBaseService<T : Any, S : Any> : JpaEntityService<T, S> {
    fun getFoo(): Boolean
}
