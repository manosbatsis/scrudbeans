package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService

interface CustomBaseService<T : Any, S> : JpaPersistableModelService<T, S>{
    fun getFoo(): Boolean
}
