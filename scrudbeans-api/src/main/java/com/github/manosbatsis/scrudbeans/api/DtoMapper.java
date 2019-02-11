package com.github.manosbatsis.scrudbeans.api;

/**
 * Generic entity from/to DTO mapper interface
 * @param <E> the entity type
 * @param <D> the DTO type
 */
public interface DtoMapper<E, D> {
	E dtoToDomain(final D order);

	D domainToDto(final E order);
}
