package com.serendib.museum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot application class for Serendib Museum Management System.
 *
 * Features:
 * - JWT-based authentication and authorization
 * - Role-based access control (RBAC)
 * - PostgreSQL database integration
 * - Automatic JPA auditing (createdAt, updatedAt, createdBy, updatedBy)
 * - Swagger/OpenAPI documentation available at /swagger-ui.html
 * - RESTful API with comprehensive CRUD operations
 * - Global exception handling
 * - Request/Response logging
 * - CORS configuration
 */
@SpringBootApplication
public class MuseumApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuseumApplication.class, args);

        System.out.println("\nTPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPW");
        System.out.println("Q                                                                   Q");
        System.out.println("Q          Serendib Museum Management API Started!                 Q");
        System.out.println("Q                                                                   Q");
        System.out.println("Q  Server:      http://localhost:8080                              Q");
        System.out.println("Q  Swagger UI:  http://localhost:8080/swagger-ui.html              Q");
        System.out.println("Q  API Docs:    http://localhost:8080/api-docs                     Q");
        System.out.println("Q                                                                   Q");
        System.out.println("Q  Database:    PostgreSQL (museumdb)                              Q");
        System.out.println("Q                                                                   Q");
        System.out.println("Q  To test the API:                                                Q");
        System.out.println("Q  1. Open Swagger UI in your browser                              Q");
        System.out.println("Q  2. Register a user via /api/v1/auth/register                    Q");
        System.out.println("Q  3. Login via /api/v1/auth/login to get JWT token                Q");
        System.out.println("Q  4. Click 'Authorize' button and enter: Bearer <your_token>     Q");
        System.out.println("Q  5. Test protected endpoints                                     Q");
        System.out.println("Q                                                                   Q");
        System.out.println("ZPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP]\n");
    }
}
