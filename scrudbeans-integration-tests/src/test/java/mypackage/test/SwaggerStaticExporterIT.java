package mypackage.test;


import lombok.extern.slf4j.Slf4j;
import mypackage.ScrudBeansSampleApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * {@inheritDoc}
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ScrudBeansSampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SwaggerStaticExporterIT extends com.github.manosbatsis.scrudbeans.test.SwaggerStaticExporterIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerStaticExporterIT.class);

}
