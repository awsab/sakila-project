# ==============================================================================
# New-SakilaModule.ps1
#
# Scaffolds a new Sakila microservice module following the established
# patterns in customer-service and inventory-service.
#
# Usage:
#   .\New-SakilaModule.ps1 -ServiceName "film-service" -Port 8091 -Pkg "filmservice"
#
# After scaffolding, add your entities then call:
#   Generate-EntityFiles -Entity "Film" -ServiceName "film-service" -Pkg "filmservice"
# ==============================================================================

param(
    [Parameter(Mandatory, HelpMessage = "Module folder name, e.g. film-service")]
    [string]$ServiceName,

    [Parameter(Mandatory, HelpMessage = "Server port, e.g. 8091")]
    [int]$Port,

    [Parameter(Mandatory, HelpMessage = "Java sub-package (no hyphens), e.g. filmservice")]
    [string]$Pkg
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# ==============================================================================
# Helpers
# ==============================================================================
function Write-File {
    param([string]$Path, [string]$Content)
    $dir = Split-Path $Path -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
    [System.IO.File]::WriteAllText($Path, $Content, [System.Text.UTF8Encoding]::new($false))
    Write-Host "  [created] $(Split-Path $Path -Leaf)" -ForegroundColor DarkGray
}

function ToPascalCase([string]$s) {
    ($s -split '-' | ForEach-Object { (Get-Culture).TextInfo.ToTitleCase($_.ToLower()) }) -join ''
}

# ==============================================================================
# Paths
# ==============================================================================
$Root      = $PSScriptRoot
$SvcRoot   = Join-Path $Root $ServiceName
$JavaBase  = "com\me\learning\parent\$Pkg"
$JavaSrc   = Join-Path $SvcRoot "src\main\java\$JavaBase"
$JavaTest  = Join-Path $SvcRoot "src\test\java\$JavaBase"
$Resources = Join-Path $SvcRoot "src\main\resources"
$TestRes   = Join-Path $SvcRoot "src\test\resources"
$NewPkg    = "com.me.learning.parent.$Pkg"
$ClassName = ToPascalCase $ServiceName

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Scaffolding: $ServiceName" -ForegroundColor Cyan
Write-Host "  Port       : $Port" -ForegroundColor Cyan
Write-Host "  Package    : $NewPkg" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# ==============================================================================
# 1. Directory tree
# ==============================================================================
Write-Host "`n[1/7] Creating directory tree..."
@("config","controller","dto","entity","mapper","repository","service") |
    ForEach-Object { New-Item -ItemType Directory -Force -Path (Join-Path $JavaSrc $_) | Out-Null }
# Test source directories
@("controller","service") |
    ForEach-Object { New-Item -ItemType Directory -Force -Path (Join-Path $JavaTest $_) | Out-Null }
New-Item -ItemType Directory -Force -Path $Resources | Out-Null
New-Item -ItemType Directory -Force -Path $TestRes   | Out-Null

# ==============================================================================
# 2. Maven wrapper
# ==============================================================================
Write-Host "[2/7] Copying Maven wrapper..."
$Ref = Join-Path $Root "customer-service"
Copy-Item (Join-Path $Ref "mvnw")     $SvcRoot -Force
Copy-Item (Join-Path $Ref "mvnw.cmd") $SvcRoot -Force
if (Test-Path (Join-Path $Ref ".mvn")) {
    Copy-Item (Join-Path $Ref ".mvn") (Join-Path $SvcRoot ".mvn") -Recurse -Force
}

# ==============================================================================
# 3. pom.xml
# ==============================================================================
Write-Host "[3/7] Writing pom.xml..."
$PomContent = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.me.learning.parent</groupId>
        <artifactId>awsab-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../awsab-parent/pom.xml</relativePath>
    </parent>

    <groupId>com.me.learning.parent</groupId>
    <artifactId>$ServiceName</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>$ServiceName</name>
    <description>$ServiceName</description>

    <dependencies>

        <dependency>
            <groupId>com.me.learning.parent</groupId>
            <artifactId>awsab-framework</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <!-- ============================================
             TESTING
        ============================================ -->

        <!-- Spring Boot test slice (JUnit 5, Mockito, AssertJ, MockMvc, etc.) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- In-memory database for @DataJpaTest slices -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- AssertJ  -  fluent assertions -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- REST Assured  -  HTTP integration testing DSL
             Version managed by awsab-parent dependencyManagement.
             Remove these two dependencies if REST Assured is not needed
             in this module. -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- REST Assured JSON path support -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>json-path</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <!-- Failsafe  -  runs *IT.java integration tests -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
"@
Write-File -Path (Join-Path $SvcRoot "pom.xml") -Content $PomContent

# ==============================================================================
# 4. application.yaml
# ==============================================================================
Write-Host "[4/7] Writing application.yaml..."
$AppYaml = @"
spring:
  application:
    name: $ServiceName

  datasource:
    url: jdbc:mysql://localhost:3306/sakila?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: Oman@2017
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

server:
  port: $Port

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operationsSorter: method
    tagsSorter: alpha

logging:
  level:
    com.me.learning.parent.${Pkg}: INFO
    com.me.learning.parent.${Pkg}.config.LoggingInterceptor: INFO
    org.springframework.web: INFO
"@
Write-File -Path (Join-Path $Resources "application.yaml") -Content $AppYaml

# ==============================================================================
# 5. application-test.yaml  (H2 in-memory for integration tests)
# ==============================================================================
Write-Host "[5/7] Writing application-test.yaml..."
$TestYaml = @"
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect

  cache:
    type: none

logging:
  level:
    root: WARN
    com.me.learning: INFO
"@
Write-File -Path (Join-Path $TestRes "application-test.yaml") -Content $TestYaml

# ==============================================================================
# 6. Config classes (exceptions + logging are provided by awsab-framework)
# ==============================================================================
Write-Host "[6/7] Writing config classes..."
#
# NOTE: The following are NO LONGER scaffolded per-service because they live in
#       awsab-framework and are auto-registered via Spring Boot auto-configuration:
#
#   - exception/ResourceNotFoundException.java      -> com.me.learning.framework.web.errors
#   - exception/DuplicateResourceException.java     -> com.me.learning.framework.web.errors
#   - exception/InvalidRequestException.java        -> com.me.learning.framework.web.errors
#   - exception/ServiceException.java               -> com.me.learning.framework.web.errors
#   - exception/GlobalExceptionHandler.java         -> com.me.learning.framework.web.errors (auto-configured)
#   - config/LoggingInterceptor.java                -> com.me.learning.framework.web.config (auto-configured)
#   - config/WebMvcConfig.java                      -> com.me.learning.framework.web.config (auto-configured)
#
# Services only need to declare CacheConfig (entity-specific) and OpenAPIConfig (service-specific).

# --- CacheConfig (needs entity names added later) ----
$CacheConfig = @"
package $NewPkg.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Usage    : Caching configuration  -  add one singular + one plural name per entity
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager () {
        // TODO: replace with real entity cache names
        return new ConcurrentMapCacheManager ("items", "item");
    }
}
"@
Write-File -Path (Join-Path $JavaSrc "config\CacheConfig.java") -Content $CacheConfig

# --- OpenAPIConfig ---
$OpenAPI = @"
package $NewPkg.config;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Usage    : OpenAPI / Swagger configuration for $ServiceName
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI ${Pkg}OpenAPI () {
        Server localServer = new Server ();
        localServer.setUrl ("http://localhost:$Port");
        localServer.setDescription ("Local Development Server");

        Contact contact = new Contact ();
        contact.setName ("Prabakaran Ramu");
        contact.setEmail ("ramup@example.com");

        License license = new License ()
                .name ("MIT License")
                .url ("https://choosealicense.com/licenses/mit/");

        Info info = new Info ()
                .title ("$ServiceName API")
                .version ("1.0.0")
                .contact (contact)
                .description ("REST API for managing Sakila $ServiceName resources")
                .license (license);

        return new OpenAPI ()
                .info (info)
                .servers (List.of (localServer));
    }
}
"@
Write-File -Path (Join-Path $JavaSrc "config\OpenAPIConfig.java") -Content $OpenAPI

# ==============================================================================
# 7. Main Application class
# ==============================================================================
Write-Host "[7/7] Writing ${ClassName}Application.java..."
$AppClass = @"
package $NewPkg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ${ClassName}Application {

    public static void main (String[] args) {
        SpringApplication.run (${ClassName}Application.class, args);
    }
}
"@
Write-File -Path (Join-Path $JavaSrc "${ClassName}Application.java") -Content $AppClass

# ==============================================================================
# Done
# ==============================================================================
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Module scaffold complete!" -ForegroundColor Green
Write-Host "  Path: $SvcRoot" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Add @Entity classes to: $JavaSrc\entity\"
Write-Host "  2. Run Generate-EntityFiles for each entity (see MICROSERVICE_RUNBOOK.md)"
Write-Host "     Each entity generates:"
Write-Host "       - DTO, Mapper, Repository, Service, Controller (main source)"
Write-Host "       - <Entity>ServiceImplTest.java  (Mockito unit test)" -ForegroundColor Magenta
Write-Host "       - <Entity>ControllerIT.java     (REST Assured integration test)" -ForegroundColor Magenta
Write-Host "  3. Update CacheConfig with your entity cache names"
Write-Host "  4. Update OpenAPIConfig title/description for this service"
Write-Host "  5. Exceptions (ResourceNotFoundException, DuplicateResourceException, etc.)"
Write-Host "     and LoggingInterceptor/WebMvcConfig come FREE from awsab-framework -- no code needed!"
Write-Host "  6. Update VALID_JSON in *ControllerIT.java to match actual required request fields"
Write-Host "  7. Adjust *ServiceImplTest.java if entity has FK-dependent repositories"
Write-Host "  8. Run: cd $SvcRoot ; .\mvnw.cmd compile"
Write-Host "  9. Run: cd $SvcRoot ; .\mvnw.cmd test              (unit tests)"
Write-Host " 10. Run: cd $SvcRoot ; .\mvnw.cmd verify            (unit + integration tests)"
Write-Host ""

