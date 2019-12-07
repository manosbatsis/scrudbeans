package mykotlinpackage

import com.github.manosbatsis.scrudbeans.jpa.repository.ModelRepositoryFactoryBean
import mykotlinpackage.model.Order
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.Product
import mykotlinpackage.service.OrderLineService
import mykotlinpackage.service.OrderService
import mykotlinpackage.service.ProductService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.format.Formatter
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.math.BigDecimal
import java.text.ParseException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ErrorMvcAutoConfiguration::class]) // Enable transactions and auditing
@EnableTransactionManagement
@EnableJpaAuditing // Scan for existing or runtime-generated (scrudbeans) components
@EntityScan(ScrudBeansSampleApplication.PACKAGE_NAME)
@EnableJpaRepositories(basePackages = [ScrudBeansSampleApplication.PACKAGE_NAME], repositoryFactoryBeanClass = ModelRepositoryFactoryBean::class)
class ScrudBeansSampleApplication {

    companion object {
        const val PACKAGE_NAME = "mykotlinpackage"
    }

    /**
     * Register a formatter for parsing LocalDateTime
     * @return
     */
    @Bean
    fun localDateFormatter(): Formatter<LocalDateTime> {
        return object : Formatter<LocalDateTime> {
            @Throws(ParseException::class)
            override fun parse(text: String, locale: Locale): LocalDateTime {
                return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME)
            }

            override fun print(`object`: LocalDateTime, locale: Locale): String {
                return DateTimeFormatter.ISO_DATE_TIME.format(`object`)
            }
        }
    }

    /**
     * Create sample products for demo purposes
     */

    @Bean
    fun demo(
            orderService: OrderService,
            orderLineService: OrderLineService,
            productService: ProductService): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? ->
            // save a few products
            productService.create(Product(name = "Systemantics", description = "How Systems Work and Especially How They Fail", price = BigDecimal.valueOf(126.95)))
            productService.create(Product(name = "Design Patterns", description = "Elements of Reusable Object-Oriented Software", price = BigDecimal.valueOf(42.93)))
            productService.create(Product(name = "XML Topic Maps", description = "Creating and Using Topic Maps for the Web", price = BigDecimal.valueOf(3.44)))
            productService.create(Product(name = "LOTR 1", description = "Lord or the rings:  The Fellowship of the Ring", price = BigDecimal.valueOf(3.44)))
            productService.create(Product(name = "LOTR 2", description = "Lord or the rings:  The Two Towers", price = BigDecimal.valueOf(3.44)))
            productService.create(Product(name = "LOTR 3", description = "Lord or the rings:  The Return of the King", price = BigDecimal.valueOf(3.44)))
            // fetch all customers
            val order: Order = orderService.create(Order(email = "foo@bar.baz"))
            for (p in productService.findAll()) {
                var orderLine = OrderLine(
                        order = order,
                        product = p,
                        quantity = 2)
                orderLine = orderLineService.create(orderLine)
            }
        }
    }

    fun main(args: Array<String>) {
        runApplication<ScrudBeansSampleApplication>(*args)
    }
}