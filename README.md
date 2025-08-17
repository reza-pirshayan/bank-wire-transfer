# Bank Wire Transfer Order Approval System

## Introduction

Some problems are inherently complex—and this complexity cannot be eliminated. However, one of the most effective ways to manage complexity is to decompose the problem into smaller, understandable parts. This principle applies equally to software projects.

In software engineering, the goal is to produce systems that are understandable, testable, extensible, and scalable, regardless of how complex the domain is. Over the course of working on various projects, I’ve gained valuable experience with different architectural styles and design techniques. I’ve decided to share these experiences in the form of a relatively small but practical project. This project is designed to help developers and software architects learn and adopt solid architectural patterns. The same architectural style used here has proven successful in larger, real-world projects.

## Problem Description

Let’s assume a large organization has multiple internal systems that generate wire transfer orders—e.g., payroll systems, insurance reimbursement platforms, or other finance-related systems. These systems depend on external banking services to execute payments.

If each internal system connects directly to every supported bank, we end up with a complex mesh topology between internal systems (consumers) and banks (providers). This is hard to maintain and scale.

To manage this complexity, we propose introducing an intermediary system with the following responsibilities:

- Receive and register wire transfer orders from internal systems.
- Based on configurable rules or algorithms, send these orders to the appropriate bank.

This architecture results in a star topology that is much easier to maintain.

This project focuses solely on the **approval use case** of registered transfer orders. Specifically, before a wire transfer can be submitted to a bank, it must be approved by two authorized signers, much like dual signatures on a physical cheque.

We assume that transfer orders have already been registered in the system. The sending process and other peripheral flows are excluded from this scope.

## Architectural Approaches Used

This project employs the following design principles and patterns:

- **Domain-Driven Design (DDD)**: To model business logic using a rich domain model and ubiquitous language.
- **Clean Architecture**: To ensure layer separation, inversion of dependencies, and technology-agnostic business logic.
- **Functional Architecture**: For immutable aggregates and explicit domain state transitions.

Additionally, the following design patterns are used:

- **Builder Pattern**: For constructing complex objects with separation of construction from behavior.
- **Command Pattern (CQRS-style)**: For modeling use cases as commands within the application layer (not to be confused with GoF Command pattern).

Before diving into implementation details, it is recommended to review the Class Diagram and Activity Diagram for the wire transfer approval use case.

## Technologies Used

- **Language**: Java  
- **Framework**: Quarkus  
- **Testing**: JUnit  
- **Database**: Oracle  
- **Protocols**: REST and gRPC

## Project Layers and Package Structure

### Domain Model Layer

- **Located in**: `domain.model`
- Does **not** use any annotations (e.g., `@Entity`) to stay independent from frameworks like JPA or Quarkus.
- Contains domain entities (Aggregates) named using the Ubiquitous Language.
- Uses package-private visibility to encapsulate inner classes.
- Aggregates are **immutable**, built using the **Builder pattern**, and validate their own invariants.
- Technology-agnostic and only dependent on Java SE.

### Domain Service Layer

- **Located in**: `domain.service`
- Encapsulates business logic that spans multiple aggregates.
- Typically used when hydration from a repository is required.
- **Persistence of mutations is not allowed** here; any write operations are handled through application services.

### Application Layer

- Coordinates domain elements to execute a use case.
- Does **not** contain any business logic.
- Uses **commands and handlers** to model each use case (CQRS-style).
- Interacts with interfaces for persistence and presentation to enforce the **Dependency Inversion Principle**.

#### Main responsibilities:

- Hydrate aggregates from repositories.
- Invoke domain methods.
- Persist changes.
- Delegate result formatting to presenters.

### Infrastructure Layer

- Provides concrete implementations for:
  - Persistence repositories
  - External service integrations
  - Mappers (domain ↔ relational model)
- Uses **Panache (Quarkus)** to simplify JPA interaction.
- Maps between domain models and JPA entities.

### Controller Layer

- Exposes use cases to external systems via:
  - **REST** (HTTP/JSON)
  - **gRPC** (HTTP/2 and Protobuf)
- Allows seamless switching between monolithic or microservice deployment models.
- Presenters adapt domain responses to specific transport formats.

## Business Rules and Invariants

### First Signature

**Preconditions**:

- Order status must be `PENDING_FIRST_SIGNATURE`.
- Signer must be listed in first signer candidates.

**Effects**:

- Status changes to `PENDING_SECOND_SIGNATURE`.
- First signer candidates cleared.
- First signer info and timestamp recorded.
- Second signer candidates filtered:
  - First signer removed.
  - Only candidates with equal or higher position retained.

### Second Signature

**Preconditions**:

- Order must already be signed once.
- Signer must be in second signature candidates.
- Second signer must not be the same as the first signer.

**Effects**:

- Status changes to `PENDING_BANK_DISPATCH`.
- Second signature candidates cleared.
- Second signature info and timestamp recorded.

> All business rules are enforced and tested through **unit tests** using **Arrange → Act → Assert** structure.

## Aggregates

### AchTransferOrder Aggregate

Represents a wire transfer order. Key fields include:

- `achTransferOrderId`: unique ID  
- `receivedDateTime`: timestamp  
- `status`: current status (e.g., waiting for signature)  
- `firstSignerRuleCandidateIds`: list of first signer candidate IDs  
- `secondSignerRuleCandidateIds`: list of second signer candidate IDs
- `firstSignature` / `secondSignature`: signature info  
- `cancelDateTime`: cancel timestamp  

**Behavioral Methods**:

- `signAsFirst(...)`: Validates status, signer, and updates state.
- `signAsSecond(...)`: Validates signer and updates state.

> Uses functional domain modeling: changes result in new instances.

### FinanceOfficerRule Aggregate

Represents a finance officer's signing permissions:

- `financeOfficerRuleId`
- `isAllowedToSignAsFirst`
- `isAllowedToSignAsSecond`
- `maxFirstSignAmount`
- `maxSecondSignAmount`
- `position`: rank or role

**Validator Methods**:

- `ensureSufficientPrivilegesForFirstSignature(amount)`
- `ensureSufficientPrivilegesForSecondSignature(amount)`

> No commands exist in this aggregate as it's read-only in this project.

## Domain Service: AchTransferOrderDomainService

**Main method**: `sign(...)`

- Accepts a signer and a transfer order.
- Decides whether to apply first or second signature.
- Validates signing permissions.
- Delegates to aggregate methods for behavior.
- Uses repositories to refine candidate lists.

## Application Layer Components

- `SignAchTransferOrderCommand`: input DTO  
- `SignAchTransferOrderCommandHandler`: handles the command (core logic for executing the use case)
- `SignAchTransferOrderPresenter`: interface for presenting success/failure

## Controller Layer (REST and gRPC)

### gRPC

- `.proto` file defines service and message types  
- gRPC controller uses application service and presenter implementation

### REST

- JSON DTOs for request/response  
- REST controller uses HTTP POST and presenter

## Infrastructure: Persistence

**Located in**: `infrastructure.persistence.achtransferorder`

- **Entities**: JPA entities for order and signature data  
- **Repositories**: Implemented using `PanacheRepositoryBase`  
- **Mappers**: Convert between entity and domain objects  
- **RepositoryImpl**: Implements domain repository interface

## Data Validation

- Done within the domain layer via constructors and value objects  
- Aggregates are only instantiated if data is valid  
- Invalid data triggers `InvalidDomainException`

**Ensures**:

- Centralized validation  
- Prevention of invalid object creation  
- Protection of invariants

## Testing

**Test philosophy based on Unit Testing by Vladimir Khorikov**:

- Resistence to refactoring
- Protection against regression
- Fast feedback
- Maintainable

**Tests are organized by layer**:

- **Domain Model**: Tests aggregate behavior and invariants  
- **Domain Service**: Validates coordination logic  
- **Application Layer**: Tests use case execution  
- **Controller Layer**: Integration tests (no mocks)

## Conclusion

This project demonstrates a robust and scalable approach to building business-critical systems using:

- Domain-Driven Design (DDD)  
- Clean Architecture  
- Functional Architecture  
- REST/gRPC APIs  
- Comprehensive test coverage  

> The layered structure and clear separation of concerns make the codebase easy to understand, extend, and maintain.

You are encouraged to explore the source code, diagrams, and test cases to gain deeper insight. This project can serve as a solid foundation for implementing additional use cases (e.g., order cancellation).
