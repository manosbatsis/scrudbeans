package com.github.manosbatsis.scrudbeans.sample.test;

import com.github.manosbatsis.scrudbeans.sample.ScrudBeansSampleApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ScrudBeansSampleApplication.class)
public class SpringContextIT {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}
