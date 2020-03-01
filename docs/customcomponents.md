---
title: Custom Components
---

## Code Structure

In short, ScrudBeans reads a package structure like the one bellow. 
At a minimum, ScrudBeans will process packages containing entity models and 
take a note of any optional, pre-existing custom components:

```
└── mypackage
    ├── controller
    │   └── OrderLineController
    └── dto
    │   └── OrderEmailUpdateDTO
    └── model
        ├── OrderLine
        ├── Order
        └── Product
```

To use your custom components, all you have to do is create them yourself - like the `OrderLineController` 
above. ScrudBeans will preserve any existing components and complete the structure needed for SCRUD as follows: 

```
└── mypackage
    ├── controller
    │   ├── OrderLineController
    │   ├── OrderController
    │   └── ProductController
    └── dto
    │   └── OrderEmailUpdateDTO
    └── mapper
    │   └── OrderToOrderEmailUpdateDTOMapper
    ├── model
    │   ├── OrderLine
    │   ├── Order
    │   └── Product
    ├── repository
    │   ├── OrderLineRepository
    │   ├── OrderRepository
    │   └── ProductRepository
    ├── service
    │   ├── OrderLineServiceImpl
    │   ├── OrderLineService
    │   ├── OrderServiceImpl
    │   ├── OrderService
    │   ├── ProductServiceImpl
    │   └── ProductService
    └── specification
        ├── AnyToOneOrderLinePredicateFactory
        ├── AnyToOneOrderPredicateFactory
        └── AnyToOneProductPredicateFactory
```

When using Maven, you can browse the generated component sources in `target/generated-sources/annotations`.
Gradle users can browse generated sources at `build/generated/source/apt/main`.

### Default Components

ScrudBeans extends existing classes to generating components for your application. You can override 
the default base classes globally in your __application.properties__:

| Property                             	| Default Value                                                                       	|
|--------------------------------------	|-------------------------------------------------------------------------------------	|
| scrudbeans.processor.jpa.repository  	| com.github.manosbatsis.scrudbeans.common.repository.ModelRepository                 	|
| scrudbeans.processor.jpa.service     	| com.github.manosbatsis.scrudbeans.common.service.PersistableModelService            	|
| scrudbeans.processor.jpa.serviceImpl 	| com.github.manosbatsis.scrudbeans.jpa.service.AbstractPersistableModelServiceImpl   	|
| scrudbeans.processor.jpa.controller  	| com.github.manosbatsis.scrudbeans.jpa.controller.AbstractPersistableModelController 	|

### Generated Sources 

| Source   	| Build   	| Path                                        	|
|--------	|---------	|---------------------------------------------	|
| Java   	| Maven   	| target/generated-sources/annotations        	|
| Kotlin 	| Maven   	| target/generated-sources/kaptKotlin/compile 	|
| Kotlin 	| Grtadle 	| build/generated/source/kapt/main            	|

## Custom Components

ScrudBeans uses a 3-tier architecture with controllers, services and repositories to provide SCRUD services 
around each entity model. The following sections introduce the base components used by ScrudBeans and 
explain how those can be extended to make your own custom components if needed.

You can __override any generated component__ by adding a custom implementation to your main sources, 
using the same package and classname. 

Moving generated components to main sources is the natural way to create a custom component, 
as most tend to be thin generic types with no other code. 

### Repositories

To override th a custom repository, all you have to do is create them e.g. like the `OrderLineRepository` bellow.

```
└── mypackage
    ├── repository
    │   └── OrderLineRepository
    └── model
        └── OrderLine
```

The implementation is actually a common Spring Data repository interface that extends 
`ModelRepository<ENTITY_TYPE, ID_TYPE>`:

```java
@Repository
public interface OrderLineRepository extends ModelRepository<OrderLine, String> {
	// Custom method!
    Optional<OrderLine> findOneByFoo(String foo);
}

```

### Services

To use your custom services, all you have to do is create them e.g. like the `OrderLineService` 

bellow.

```
└── mypackage
    ├── service
    │   └── OrderLineService
    └── model
        └── OrderLine
```

The service can either be a concrete class like: 

```java
public class OrderLineService  
	extends AbstractPersistableModelServiceImpl<OrderLine, String, OrderLineRepository>
	implements PersistableModelService<OrderLine, String> {
	// Custom methods...	
	Optional<OrderLine> findOneByFoo(String foo) {
		return this.repository.findOneByFoo(foo);
	}
	
}
```

or, if preferred, an interface and separate implementation:

```java
// For the interface, extend PersistableModelService<<ENTITY_TYPE, ID_TYPE>>
public interface OrderLineService 
	extends PersistableModelService<OrderLine, String> {
	//custom methods...	
	Optional<OrderLine> findOneByFoo(String foo);
}
	
// For the implementation, extend AbstractPersistableModelServiceImpl<<ENTITY_TYPE, ID_TYPE, REPO_TYPE>>
@Service("orderLineService")
public class OrderLineServiceImpl 
	extends AbstractPersistableModelServiceImpl<OrderLine, String, OrderLineRepository> 
	implements OrderLineService {
	// Custom methods...
	Optional<OrderLine> findOneByFoo(String foo) {
		return this.repository.findOneByFoo(foo);
	}
}
```

### Controllers

To use your custom controllers, create them yourself e.g. like the `OrderLineController` 
bellow.

```
└── mypackage
    ├── controller
    │   └── OrderLineController
    └── model
        └── OrderLine
```

The implementation can extend  
`AbstractPersistableModelController<ENTITY_TYPE, ID_TYPE, SERVICE_TYPE>`:

```java
@RestController("orderLineController")
@RequestMapping("/api/rest/orderLines")
@ExposesResourceFor(OrderLine.class)
public class OrderLineController 
extends AbstractPersistableModelController<OrderLine, String, OrderLineService> {
	// Custom methods...
	@GetMapping("foo/{foo}")
	public OrderLine findByFoo(@PathVariable String foo) {
		return this.service.findOneByFoo(foo).orElseThrow(() ->
			// Let scrudbeans-error create a 404 JSON response
			new NotFoundException("No match for foo: " + foo));
	}
}
```

### DTO Mappers

Most of the time, you can automatically generate [MapStruct](http://mapstruct.org/)-based mappers for your DTOs by 
adding those to your `@ScrudBean` `dtoTypes` or `dtoTypeNames` attributes like so:

```java
@ScrudBean(dtoTypes = {OrderUpdateEmailDTO.class}, dtoTypeNames = {"mypackage.dto.OrderUpdateCommentDTO"})
public class Order {
	//...
}
```

Sometimes however, you might want to edit the mappings your self by creating a custom mapper, 
like `OrderToOrderUpdateCommentDTOMapper` bellow:

```
└── mypackage
    ├── dto
    │   └── OrderUpdateCommentDTO
    ├── model
    │   └── Order
    └── mapper
        └── OrderToOrderUpdateCommentDTOMapper
```

The `OrderToOrderUpdateCommentDTOMapper` implementation can be a MapStruct-based interface that simply extends 
`DtoMapper<ENTITY_TYPE, DTO_TYPE>`:

```java
import com.github.manosbatsis.scrudbeans.api.DtoMapper;
import mypackage.dto.OrderUpdateCommentDTO;
import mypackage.model.Order;
import org.mapstruct.Mapper;

@Mapper(
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
    componentModel = "spring"
)
public interface OrderToOrderUpdateCommentDTOMapper extends DtoMapper<Order, OrderUpdateCommentDTO> {
	// your custom mappings here...
}
```

MapStruct will pick up the interface and generate the actual implementation as usual - the MapStruct annotation 
processor is a transitive dependency of `scrudbeans-annotation-processor-java` 
and `scrudbeans-annotation-processor-kotlin` modules.

