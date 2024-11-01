package com.example.echo.global.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server

@OpenAPIDefinition(
    info = Info(
        title = "Echo",
        version = "ver 0.1",
        description = "Echo(RESTful API Documentation)"
    ),
    servers = [
        Server(
            description = "Prod ENV",
            url = "http://localhost:8000/"
        ),
        Server(
            description = "Staging ENV",
            url = "http://localhost:8000/staging"
        )
    ]
)
class SwaggerOpenAPIConfig