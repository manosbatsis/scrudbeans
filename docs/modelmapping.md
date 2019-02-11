---
title: Model Mapping
---

Once you have added ScrudBeans in your project, you can have it process your entity models to  
create SCRUD components and services everytime your build runs.

> At the moment, only JPA entities are supported. Future versions may support additional Spring Data sub-projects. 

## Mapping Prerequisites

Model driven services are enabled per entity provided two conditions are met:
 
 - It must be annotated with `@ScrudBean`.
 - It must implement `PersistableModel` or simply extend one of the abstract entities from the 
`com.github.manosbatsis.scrudbeans.jpa.model` package.

> The last requirement will be removed in upcpming versions.

To make this easier, ScrudBeans provides a set of mapped superclasses you can extend to create your
entities, see [Application Components: Entity Models](customcomponents#entity-models) for more details.

## Mapping Example
 
 As an example, consider an `Order` entity. The scrudbeans-template repo has the 
 [full source](https://github.com/manosbatsis/scrudbeans-template/blob/master/src/main/java/mypackage/model/Order.java).
 
 ```java
@Entity
@Table(name = "product_orders")
@ScrudBean
public class Order extends AbstractSystemUuidPersistableModel {

	// ScrudBeans will automatically pick-up Bean Validation and Column annotations 
	// to validate e.g. for both not-null and unique values
	@NotNull
	@Column(nullable = false, unique = true)
	private String email;
	
	// other members...
}
```

That is enough for ScrudBeans to create the appropriate components and expose RESTful services for 
SCRUD, meaning _Search, Create, Update and Delete_ operations for the entity type. This is explained 
further in the next chapter.

## Relevant Annotations

### ScrudBean

The `@ScrudBean` annotation marks the entity model for annotation processing and, optionally, provides relevant metadata.
The annotation processors have a reasonable strategy for creating those metadata themselves when missing. The following shorthand 
for example:

```java
@ScrudBean
public class DiscountCode {//...
```
Is equivalent to:

```java
@ScrudBean(
	pathFragment = "discountCodes", 
	apiName = "Discount Codes", 
	apiDescription = "Search or manage Discount Code entries")
public class DiscountCode { //...
```

About the annotation members:

- `pathFragment` is the path fragment appended to the API base path (by default `api/rest/`), thus forming the base 
`RequestMapping` path for this entity's REST controller.
- `apiName` is equivalent to `io.swagger.annotations.Api#tags` and serves as a name and logical grouping of 
operations/endpoints for this entity's REST controller .
- `apiDescription` is equivalent to `io.swagger.annotations.Api#description`.
- `dtoTypes` an array of DTO classes to generate mappers for.
- `dtoTypeNames` an array of DTO (canonical) class names to generate mappers for.


### Validation

- `javax.persistence.Column` for not-null and unique values
- `javax.validation.constraints` annotations
- Any custom Java Bean Validation annotation. See `com.github.manosbatsis.scrudbeans.jpa.validation.Unique` for an 
example, or create your own.

### Documentation

Annotating your models and their fields properly with `io.swagger.annotations.ApiModel` and 
`io.swagger.annotations.ApiModelProperty` respectively will increase the quality of the generated Springfox/Swagger 
documentation.