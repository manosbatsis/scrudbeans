package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.JpaEntityProjectorService

interface CustomBaseService<T : Any, S : Any> : JpaEntityProjectorService<T, S> {
    fun getFoo(): Boolean
}
