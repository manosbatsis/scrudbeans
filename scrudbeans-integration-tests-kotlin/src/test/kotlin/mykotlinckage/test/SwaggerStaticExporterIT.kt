package mykotlinpackage.test

import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * {@inheritDoc}
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [mykotlinpackage.ScrudBeansSampleApplication::class], webEnvironment = RANDOM_PORT)
object SwaggerStaticExporterIT : com.github.manosbatsis.scrudbeans.test.SwaggerStaticExporterIT() {
    private val LOGGER = LoggerFactory.getLogger(SwaggerStaticExporterIT::class.java)
}
 */