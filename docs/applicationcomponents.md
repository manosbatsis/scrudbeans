---
title: Application Components
---

In short, ScrudBeans reads a package structure like the one bellow:


```
└── mypackage
    ├── controller
    │   └── OrderLineController.java
    └── model
        ├── OrderLine.java
        ├── Order.java
        └── Product.java
```

To use your custom components, all you have to do is create them yourself - like the `OrderLineController` 
above. ScrudBeans will preserve any existing components and complete the structure as follows; 

```
└── mypackage
    ├── controller
    │   ├── OrderLineController.java
    │   ├── OrderController.java
    │   └── ProductController.java
    ├── model
    │   ├── OrderLine.java
    │   ├── Order.java
    │   └── Product.java
    ├── repository
    │   ├── OrderLineRepository.java
    │   ├── OrderRepository.java
    │   └── ProductRepository.java
    ├── service
    │   ├── OrderLineServiceImpl.java
    │   ├── OrderLineService.java
    │   ├── OrderServiceImpl.java
    │   ├── OrderService.java
    │   ├── ProductServiceImpl.java
    │   └── ProductService.java
    └── specification
        ├── AnyToOneOrderLinePredicateFactory.java
        ├── AnyToOneOrderPredicateFactory.java
        └── AnyToOneProductPredicateFactory.java
```

When using Maven, you can browse the generated component sources in `target/generated-sources/annotations`.

