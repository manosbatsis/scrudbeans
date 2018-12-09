package com.github.manosbatsis.scrudbeans.sample.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.restdude.mdd.registry.ModelInfoRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScrudBeansSampleApplicationIintegrationTest {

	@Autowired
	private ModelInfoRegistry modelInfoRegistry;

	@Test
	public void test_givenRegistry_ifNotEmptyEntries_thenSuccess() {
		log.info("givenRegistry_ifNotEmptyEntries_thenSuccess, modelInfoRegistry: {}", modelInfoRegistry);
		log.info("givenRegistry_ifNotEmptyEntries_thenSuccess, entries: {}", modelInfoRegistry.getEntries());
		assertNotNull(modelInfoRegistry.getEntries());
		assertTrue(modelInfoRegistry.getEntries().size() > 0);
	}

	@Test
	public void givenRegistry_ifNotEmptyTypes_thenSuccess() {
		log.info("givenRegistry_ifNotEmptyEntries_thenSuccess, modelInfoRegistry: {}", modelInfoRegistry);
		log.info("givenRegistry_ifNotEmptyEntries_thenSuccess, types: {}", modelInfoRegistry.getTypes());
		assertNotNull(modelInfoRegistry.getTypes());
		assertTrue(modelInfoRegistry.getTypes().size() > 0);
	}

}
