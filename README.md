# spaceX

## Overview

This project implements a clean and modular structure for managing SpaceX Dragon Rockets data. 
The architecture is divided into two main layers:

- **Repository Layer** – Responsible for communication with external clients.
- **Business Logic Layer** – Contains core domain logic and rules.

Each of these layers is further divided into two distinct responsibilities:

- **Rocket-related functionality**
- **Mission-related functionality**

## Architecture

To promote maintainability and testability, each layer and responsibility is abstracted using interfaces. 
This design choice aligns with **SOLID** and **OOP** principles, particularly the Dependency Inversion Principle, 
allowing for easier future modifications and flexible testing strategies.

### Key Design Features

- **Separation of Concerns**: Clear boundaries between business logic and data access.
- **Interface-Driven Development**: All components are developed against interfaces for maximum decoupling.
- **Testability**: Each part of the system can be tested independently using fake implementations.
- **Scalability**: Easily extendable to accommodate new features or data sources.
- **Dependency Injection**: Dependencies are injected rather than hard-coded, improving modularity and enabling easier unit testing.
- **Design Patterns**: Simple and well-established design patterns such as Builder have been applied where.


## Development Approach

The solution was developed using **TDD**. 
Fake environments with simplified implementations were used in tests by injecting them via existing interfaces. 
This ensured that all components were properly isolated and tested.

## Business Logic

All business requirements, including edge cases, have been implemented as described in the assignment. 
In cases where the specification was unclear (e.g., whether rockets should be removed from missions), 
the provided example was used as a reference for decision-making.