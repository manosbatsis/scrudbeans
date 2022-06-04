package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService

interface CustomBaseService<T : Any, S : Any> : JpaPersistableModelService<T, S> {
    fun getFoo(): Boolean
}
