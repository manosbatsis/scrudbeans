// -------------------- DO NOT EDIT -------------------
//  This file is automatically generated by scrudbeans,
//  see https://manosbatsis.github.io/scrudbeans
//  To edit this file, copy it to the appropriate package 
//  in your src/main/kotlin folder and edit there. 
// ----------------------------------------------------
package mykotlinpackage.repository

import com.github.manosbatsis.scrudbeans.repository.JpaEntityProjectorRepository
import kotlin.Long
import mykotlinpackage.model.DiscountCode
import org.springframework.stereotype.Repository

@Repository
public interface DiscountCodeRepository : JpaEntityProjectorRepository<DiscountCode, Long>
