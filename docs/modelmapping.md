---
title: Model Mapping
---

Once you have added ScrudBeans in your project, you can have it process your entity models to  
create SCRUD components and services everytime your build runs.

> At the moment, only JPA entities are supported. Future versions may support additional Spring Data sub-projects. 

## Mapping Prerequisites

Model driven services are enabled per entity provided two conditions are met:
 
 - It must be annotated with `@ScrudBean`.
 - It must have a non-primitive id.
 - It must implement `com.github.manosbatsis.scrudbeans.api.domain.Persistable` or simply extend one of the abstract 
 entities from the `com.github.manosbatsis.scrudbeans.jpa.model` package.

> The last requirement will be removed in upcpming versions.

To make this easier, ScrudBeans provides a set of mapped superclasses you can extend to create your
entities, see [Base Models](#base-models) for more details.

## Mapping Examples
 
 As an example, consider an `Order` entity in 
 [Java](https://github.com/manosbatsis/scrudbeans-template-java/blob/master/src/main/java/myjavapackage/model/Order.java) or 
 [Kotlin](https://github.com/manosbatsis/scrudbeans-template-kotlin/blob/master/src/main/kotlin/mykotlinpackage/model/Order.kt).
 
 Java example:
 
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
 Kotlin example:
 
 ```kotlin
@Entity
@Table(name = "product_orders")
@ScrudBean
data class Order(

        @field:Id
        @field:GeneratedValue(generator = "system-uuid")
        @field:GenericGenerator(name = "system-uuid", strategy = "uuid2")
        var id: String? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:ApiModelProperty(value = "The client's email", required = true)
        var email: String? = null,

        // other constructor params...


) : Persistable<String> {
        override fun getScrudBeanId() = id!!
        override fun isNew(): Boolean = id == null
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


## Base Models

Model driven services are enabled per entity provided two conditions are met:
 
 - It must be annotated with `@ScrudBean`.
 - It must implement `PersistableModel` or simply extend one of the abstract entities from the 
`com.github.manosbatsis.scrudbeans.jpa.model` package. 

To make this easier, ScrudBeans provides the `com.github.manosbatsis.scrudbeans.jpa.model` package, 
a set of mapped superclasses you can extend to create your entities.

### UUID

To use automatically generated UUIDs as a primary key you can extend `AbstractSystemUuidPersistableModel`:

```java
@Entity
@Table(name = "products")
@ScrudBean
public class Product extends AbstractSystemUuidPersistableModel {/* ...*/ }
```

### Auto Increment

You can auto-increment long IDs (i.e. `javax.persistence.GenerationType.AUTO`) you can extend
`AbstractAutoGeneratedLongIdPersistableModel`:

```java
@Entity
@Table(name = "discount_code")
@ScrudBean
public class DiscountCode extends AbstractAutoGeneratedLongIdPersistableModel {/* ...*/ }
```

### Assigned IDs

To use assigned (i.e. manual) IDs, you can extend `AbstractAssignedIdPersistableModel`. It's generic, 
so you can specify the ID type required. e.g. for `Long`:

```java
@Entity 
@ScrudBean
@Table(name = "slot")
public class Slot extends AbstractAssignedIdPersistableModel<Long> {/* ...*/ }
```

You can use `@AttributeOverrides` to apply more specific restrictions, for example a string with a 
maximum length of two characters:

```java
@Entity 
@ScrudBean
@Table(name = "country")
@AttributeOverrides({
		@AttributeOverride(name = "id", column = @Column(unique = true, nullable = false, length = 2)),
})
public class Country extends AbstractAssignedIdPersistableModel<String> {/* ...*/ }
```

### Composite IDs

Some times you need to use composite IDs in your models, e.g. when working with a legacy database design. 
This introduces some complexity in a number of areas where the ID must be handled in a regular RESTful way, 
including request mapping bindings of path or query parameters.

To help making this work transparently out of the box, ScrudBeans provides a number of embeddable ID 
implementations you can extend from depending on the number of columns/fields needed for your composite ID, 
namely `AbstractEmbeddableManyToManyIdentifier`, `AbstractEmbeddableTripleIdentifier`, 
`AbstractEmbeddableQuadrupleIdentifier` and `AbstractEmbeddableQuintupleIdentifier`.

> You can create custom composite IDs by implementing `EmbeddableCompositeIdentifier`.

For an example, consider a case where you need to assign additional attributes to a many-to-many 
relationship between models, like a `Friendship` between two `User`s, along with the requirement 
of using their two user keys as a composite ID. For cases like these, you can easily create a mapping 
that "just works" in two steps. 

First, begin by extending `AbstractEmbeddableManyToManyIdentifier` 
to create a new `@Embeddable` ID type:
 

```java
@Embeddable
public class FriendshipIdentifier 
	extends AbstractEmbeddableManyToManyIdentifier<User, String, User, String> 
	implements Serializable {

	@Override
	public User buildLeft(Serializable left) {
		User u = new User();
		u.setId(left.toString());
		return u;
	}

	@Override
	public User buildRight(Serializable right) {
		User u = new User();
		u.setId(right.toString());
		return u;
	}

}
```

Depending on the identifier superclass, you'll need to implement a number of `buildX` methods as above. 

Then, use the new `@Embeddable` identifier as the entity ID type of a `PersistableModel`:

```java
@ScrudBean
@Entity
@Table(name = "friendship")
public class Friendship implements PersistableModel<FriendshipIdentifier> {
	
	@NotNull
	@ApiModelProperty(required = true)
	@EmbeddedId
	private FriendshipIdentifier id;
	
	// Other members...
	
}
```

