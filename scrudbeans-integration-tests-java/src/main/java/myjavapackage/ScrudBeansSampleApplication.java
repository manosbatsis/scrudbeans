package myjavapackage;

import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService;
import lombok.extern.slf4j.Slf4j;
import myjavapackage.model.Order;
import myjavapackage.model.OrderLine;
import myjavapackage.model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.Formatter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
// Remove security and error handling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
// Enable transactions and auditing
@EnableTransactionManagement
@EnableJpaAuditing
// Scan for existing or runtime-generated (scrudbeans) components
@EntityScan(ScrudBeansSampleApplication.PACKAGE_NAME)
@EnableJpaRepositories(basePackages = {ScrudBeansSampleApplication.PACKAGE_NAME})
public class ScrudBeansSampleApplication {

	public static final String PACKAGE_NAME = "myjavapackage";

	public static void main(String[] args) {
		SpringApplication.run(ScrudBeansSampleApplication.class, args);
	}

	/**
	 * Register a formatter for parsing LocalDateTime
	 * @return
	 */
	@Bean
	public Formatter<LocalDateTime> localDateFormatter() {
		return new Formatter<LocalDateTime>() {
			@Override
			public LocalDateTime parse(String text, Locale locale) throws ParseException {
				return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
			}

			@Override
			public String print(LocalDateTime object, Locale locale) {
				return DateTimeFormatter.ISO_DATE_TIME.format(object);
			}
		};
	}

	/**
	 * Create sample products for demo purposes
	 */
	@Bean
	public CommandLineRunner demo(
			JpaPersistableModelService<Order, String> orderService,
			JpaPersistableModelService<OrderLine, String> orderLineService,
			JpaPersistableModelService<Product, String> productService
	) {
		return (args) -> {
			// save a few products
			productService.save(Product.builder().name("Systemantics").description("How Systems Work and Especially How They Fail").price(BigDecimal.valueOf(126.95)).build());
			productService.save(Product.builder().name("Design Patterns").description("Elements of Reusable Object-Oriented Software").price(BigDecimal.valueOf(42.93)).build());
			productService.save(Product.builder().name("XML Topic Maps").description("Creating and Using Topic Maps for the Web").price(BigDecimal.valueOf(3.44)).build());
			productService.save(Product.builder().name("LOTR 1").description("Lord or the rings:  The Fellowship of the Ring").price(BigDecimal.valueOf(3.44)).build());
			productService.save(Product.builder().name("LOTR 2").description("Lord or the rings:  The Two Towers").price(BigDecimal.valueOf(3.44)).build());
			productService.save(Product.builder().name("LOTR 3").description("Lord or the rings:  The Return of the King").price(BigDecimal.valueOf(3.44)).build());
			// fetch all customers
			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");

			Order order = orderService.save(Order.builder().email("foo@bar.baz").build());
			for (Product p : productService.findAll()) {
				OrderLine orderLine = OrderLine.builder()
						.order(order)
						.product(p)
						.quantity(2).build();
				orderLine = orderLineService.save(orderLine);
			}
			log.info("");

		};
	}
}

