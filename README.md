
# Site Master Pro - Civil Construction Project Management System

## Overview
Site Master Pro is a comprehensive web-based application designed for civil construction project management, providing real-time financial tracking, role-based access control, and operational transparency for construction projects.

## Technology Stack
- **Backend**: Spring Boot 3.2.0 with Spring Security
- **Database**: PostgreSQL with Spring Data JPA
- **Frontend**: Thymeleaf with Bootstrap 5
- **Authentication**: JWT-based with role-based access control
- **Build Tool**: Maven 3.9.6
- **Java Version**: 17

## System Features
- Multi-role access control (8 distinct roles)
- Real-time financial dashboard with live updates
- Comprehensive inventory management system
- Project timeline tracking and monitoring
- Role-specific reporting and analytics
- Complete audit trails for all transactions
- WebSocket integration for real-time notifications
- Export functionality (Excel/PDF reports)

## User Roles & Permissions
1. **Super Admin** - Full system access, user management, high-value approvals
2. **Admin** - Multi-project oversight, mid-level expenditure approval
3. **CEO** - Executive dashboards, strategic decision making
4. **Accountant** - Financial transaction management, budget tracking
5. **Store Keeper** - Inventory and material tracking, stock management
6. **Site Manager** - On-site operations oversight, progress updates
7. **Site Engineer** - Technical execution management, quality control
8. **Labor Head** - Workforce and attendance management

## Prerequisites & Dependencies
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher (or H2 for development)
- Git (for version control)

# Site Master Pro - Windows Development Setup Guide

## Prerequisites Installation

### Step 1: Install Java 17 (Required)

#### Option A: Using Chocolatey (Recommended)
```powershell
# Open PowerShell as Administrator
# Install Chocolatey if not installed
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Java 17
choco install openjdk17 -y

# Refresh environment variables
refreshenv
```

#### Option B: Manual Installation
```powershell
# Download from: https://adoptium.net/temurin/releases/?version=17
# 1. Download Windows x64 installer (.msi)
# 2. Run installer and follow wizard
# 3. Check "Set JAVA_HOME variable" option
# 4. Check "JavaSoft (Oracle) registry keys" option
```

#### Verify Java Installation
```cmd
java -version
javac -version
echo %JAVA_HOME%
```

### Step 2: Install Maven 3.9.6

#### Option A: Using Chocolatey
```powershell
# Install Maven
choco install maven -y
refreshenv
```

#### Option B: Manual Installation
```powershell
# Create tools directory
mkdir C:\tools
cd C:\tools

# Download Maven
Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" -OutFile "maven.zip"

# Extract Maven
Expand-Archive -Path "maven.zip" -DestinationPath "C:\tools\"
Rename-Item "C:\tools\apache-maven-3.9.6" "C:\tools\maven"

# Set environment variables (Run as Administrator)
[Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\tools\maven", "Machine")
[Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";C:\tools\maven\bin", "Machine")
```

#### Verify Maven Installation
```cmd
mvn -version
echo %MAVEN_HOME%
```

### Step 3: Install PostgreSQL Database

#### Option A: Using Chocolatey
```powershell
# Install PostgreSQL
choco install postgresql --params '/Password:admin123' -y
### f18060910772492c9fbb7beda55ac365
refreshenv
```

#### Option B: Manual Installation
```powershell
# Download from: https://www.postgresql.org/download/windows/
# 1. Download Windows installer
# 2. Run installer
# 3. Set password: admin123 (remember this)
# 4. Port: 5432 (default)
# 5. Locale: Default locale
```

#### Configure PostgreSQL
```cmd
# Add PostgreSQL to PATH (if not added automatically)
set PATH=%PATH%;C:\Program Files\PostgreSQL\15\bin

# Test PostgreSQL connection
psql -U postgres -h localhost
# Enter password: admin123
```

### Step 4: Install Git (Version Control)
```powershell
# Using Chocolatey
choco install git -y
refreshenv

# Manual: Download from https://git-scm.com/download/win
```

### Step 5: Install IDE (Optional but Recommended)
```powershell
# Install IntelliJ IDEA Community
choco install intellijidea-community -y

# OR Install Eclipse IDE
choco install eclipse -y

# OR Install VS Code with Java extensions
choco install vscode -y
```

## Database Setup

### Step 1: Start PostgreSQL Service
```cmd
# Start PostgreSQL service
net start postgresql-x64-15

# Or start from Services.msc
# services.msc -> Find PostgreSQL -> Right-click -> Start
```

### Step 2: Create Database and User
```cmd
# Connect to PostgreSQL as superuser
psql -U postgres -h localhost

# Inside psql prompt, execute:
```

```sql
-- Create database
CREATE DATABASE sitemasterpro;

-- Create user
CREATE USER sitemasterpro_user WITH PASSWORD 'secure_password_123';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE sitemasterpro TO sitemasterpro_user;
GRANT ALL ON SCHEMA public TO sitemasterpro_user;
ALTER USER sitemasterpro_user CREATEDB;

-- Test connection
\c sitemasterpro sitemasterpro_user;

-- Exit PostgreSQL
\q
```

### Step 3: Test Database Connection
```cmd
# Test connection with new user
psql -U sitemasterpro_user -h localhost -d sitemasterpro
# Enter password: secure_password_123
```

## Environment Variables Setup

### Method 1: Using Command Prompt (Session-based)
```cmd
# Set environment variables for current session
set DATABASE_URL=jdbc:postgresql://localhost:5432/sitemasterpro
set PGUSER=sitemasterpro_user
set PGPASSWORD=secure_password_123
set JWT_SECRET=sitemasterproSecretKeyForJWTTokenGeneration2024VerySecureKey
set SPRING_PROFILES_ACTIVE=dev
```

### Method 2: Using PowerShell (Persistent)
```powershell
# Set system environment variables (Run PowerShell as Administrator)
[Environment]::SetEnvironmentVariable("DATABASE_URL", "jdbc:postgresql://localhost:5432/sitemasterpro", "User")
[Environment]::SetEnvironmentVariable("PGUSER", "sitemasterpro_user", "User")
[Environment]::SetEnvironmentVariable("PGPASSWORD", "secure_password_123", "User")
[Environment]::SetEnvironmentVariable("JWT_SECRET", "sitemasterproSecretKeyForJWTTokenGeneration2024VerySecureKey", "User")
[Environment]::SetEnvironmentVariable("SPRING_PROFILES_ACTIVE", "dev", "User")

# Refresh environment
$env:PATH = [System.Environment]::GetEnvironmentVariable("PATH","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("PATH","User")
```

### Method 3: Using GUI
```
1. Right-click "This PC" -> Properties
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "User variables", click "New"
5. Add each variable:
   - Variable name: DATABASE_URL
   - Variable value: jdbc:postgresql://localhost:5432/sitemasterpro
   (Repeat for all variables)
6. Click OK to save
7. Restart Command Prompt/PowerShell
```

## Project Setup

### Step 1: Create Project Directory
```cmd
# Create workspace directory
mkdir C:\workspace
cd C:\workspace

# Create project directory
mkdir SiteMasterPro
cd SiteMasterPro
```

### Step 2: Initialize Maven Project
```cmd
# Generate Maven project structure
mvn archetype:generate -DgroupId=com.sitemasterpro -DartifactId=site-master-pro -DarchetypeArtifactId=maven-archetype-web -DinteractiveMode=false

cd site-master-pro
```

### Step 3: Update Project Structure
```cmd
# Create additional directories
mkdir src\main\java\com\sitemasterpro\config
mkdir src\main\java\com\sitemasterpro\controller
mkdir src\main\java\com\sitemasterpro\dto
mkdir src\main\java\com\sitemasterpro\entity
mkdir src\main\java\com\sitemasterpro\enums
mkdir src\main\java\com\sitemasterpro\exception
mkdir src\main\java\com\sitemasterpro\repository
mkdir src\main\java\com\sitemasterpro\security
mkdir src\main\java\com\sitemasterpro\service
mkdir src\main\java\com\sitemasterpro\util
mkdir src\main\java\com\sitemasterpro\websocket

mkdir src\main\resources\db\migration
mkdir src\main\resources\static\css
mkdir src\main\resources\static\js
mkdir src\main\resources\static\images
mkdir src\main\resources\templates
```

### Step 4: Configure Maven Dependencies
Create/update `pom.xml` with Spring Boot dependencies:

```cmd
# Clear default pom.xml and create new one
echo. > pom.xml
notepad pom.xml
```

### Step 5: Create Application Configuration
```cmd
# Create application.yml
echo. > src\main\resources\application.yml
notepad src\main\resources\application.yml
```

### Step 6: Create Main Application Class
```cmd
# Create main Spring Boot application class
notepad src\main\java\com\sitemasterpro\SiteMasterProApplication.java
```

## Build and Test

### Step 1: Clean and Compile
```cmd
# Clean previous builds
mvn clean

# Compile the project
mvn compile

# Check for compilation errors
echo %ERRORLEVEL%
```

### Step 2: Install Dependencies
```cmd
# Download and install all dependencies
mvn clean install -DskipTests

# If you want to run tests
mvn clean install
```

### Step 3: Verify Dependencies
```cmd
# Check dependency tree
mvn dependency:tree

# Resolve any dependency conflicts
mvn dependency:resolve
```

## Running the Application

### Step 1: Database Migration (First Time)
```cmd
# Run Flyway migrations
mvn flyway:migrate

# Or let Spring Boot handle it automatically
mvn spring-boot:run
```

### Step 2: Start the Application
```cmd
# Method 1: Using Maven Spring Boot plugin
mvn spring-boot:run

# Method 2: Build JAR and run
mvn clean package -DskipTests
java -jar target\site-master-pro-1.0.0.jar

# Method 3: Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Step 3: Access the Application
```
Open browser and navigate to:
- Local URL: http://localhost:5000
- Default Login: admin / admin123
```

## Development Tools Setup

### Step 1: Configure IDE (IntelliJ IDEA)
```
1. Open IntelliJ IDEA
2. Import Project -> Select SiteMasterPro folder
3. Import as Maven project
4. Wait for indexing to complete
5. Configure Project SDK (Java 17)
6. Enable annotation processing
7. Install plugins: Spring Boot, Thymeleaf
```

### Step 2: Database GUI Tool (Optional)
```powershell
# Install pgAdmin 4
choco install pgadmin4 -y

# Or install DBeaver
choco install dbeaver -y
```

### Step 3: Configure Git
```cmd
# Set global Git configuration
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# Initialize repository
git init
git add .
git commit -m "Initial project setup"
```

## Testing and Verification

### Step 1: Test Database Connection
```cmd
# Test with psql
psql -U sitemasterpro_user -h localhost -d sitemasterpro -c "SELECT current_database();"
```

### Step 2: Test Application Endpoints
```cmd
# Test health endpoint (after app starts)
curl http://localhost:5000/actuator/health

# Test login endpoint
curl -X POST http://localhost:5000/api/auth/signin -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

### Step 3: Verify Environment Variables
```cmd
# Check all environment variables are set
echo %DATABASE_URL%
echo %PGUSER%
echo %PGPASSWORD%
echo %JWT_SECRET%
echo %SPRING_PROFILES_ACTIVE%
```

## Common Windows-Specific Issues and Solutions

### Issue 1: Java Not Found
```cmd
# Check Java installation
where java
java -version

# If not found, add to PATH
set PATH=%PATH%;C:\Program Files\Java\jdk-17\bin
```

### Issue 2: Maven Not Found
```cmd
# Check Maven installation
where mvn
mvn -version

# If not found, add to PATH
set PATH=%PATH%;C:\tools\maven\bin
```

### Issue 3: PostgreSQL Service Issues
```cmd
# Check PostgreSQL service status
sc query postgresql-x64-15

# Start service if stopped
net start postgresql-x64-15

# If service doesn't exist, reinstall PostgreSQL
```

### Issue 4: Port Already in Use
```cmd
# Find process using port 5000
netstat -ano | findstr :5000

# Kill process (replace PID with actual process ID)
taskkill /PID 1234 /F

# Or change port in application.yml
```

### Issue 5: Permission Issues
```cmd
# Run Command Prompt as Administrator
# Right-click Command Prompt -> Run as administrator

# Or use PowerShell as Administrator
# Right-click PowerShell -> Run as administrator
```

## Production Build Commands

### Step 1: Create Production Package
```cmd
# Clean and package for production
mvn clean package -DskipTests -Pprod

# Create optimized JAR
mvn clean package -Dspring.profiles.active=prod
```

### Step 2: Test Production Build
```cmd
# Test production JAR locally
java -Dspring.profiles.active=prod -jar target\site-master-pro-1.0.0.jar
```

### Step 3: Create Distribution
```cmd
# Create distribution directory
mkdir dist
copy target\site-master-pro-1.0.0.jar dist\
copy src\main\resources\application.yml dist\
```

## Useful Development Commands

### Daily Development Workflow
```cmd
# Pull latest changes
git pull origin main

# Clean and install
mvn clean install -DskipTests

# Start development server
mvn spring-boot:run

# In another terminal - watch for changes
mvn spring-boot:run -Dspring-boot.run.fork=false
```

### Database Management
```cmd
# Drop and recreate database
psql -U postgres -c "DROP DATABASE IF EXISTS sitemasterpro;"
psql -U postgres -c "CREATE DATABASE sitemasterpro;"

# Run migrations
mvn flyway:migrate

# Reset database
mvn flyway:clean
mvn flyway:migrate
```

### Debugging Commands
```cmd
# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Check logs
type logs\site-master-pro.log

# Monitor real-time logs
powershell Get-Content -Path "logs\site-master-pro.log" -Wait
```

This guide provides all the Windows-specific commands and steps needed to set up Site Master Pro for development on a Windows machine.

## Step-by-Step Development Setup

### 1. Environment Preparation

#### On Local Machine
```bash
# Install Java 17 (Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-17-jdk

# Install Maven
sudo apt install maven

# Verify installations
java -version
mvn -version
```

### 2. Database Setup

#### Option A: PostgreSQL (Production Ready)
```sql
-- Connect to PostgreSQL as superuser
sudo -u postgres psql

-- Create database and user
CREATE DATABASE sitemasterpro;
CREATE USER sitemasterpro_user WITH PASSWORD 'secure_password_123';
GRANT ALL PRIVILEGES ON DATABASE sitemasterpro TO sitemasterpro_user;
GRANT ALL ON SCHEMA public TO sitemasterpro_user;

-- Exit PostgreSQL
\q
```

#### Option B: H2 Database (Development Only)
The application is pre-configured to use H2 in-memory database for development. No additional setup required.

### 3. Environment Variables Configuration

#### On Local Machine
```bash
# Add to ~/.bashrc or ~/.zshrc
export DATABASE_URL=jdbc:postgresql://localhost:5432/sitemasterpro
export PGUSER=sitemasterpro_user
export PGPASSWORD=secure_password_123
export JWT_SECRET=sitemasterproSecretKeyForJWTTokenGeneration2024VerySecureKey
export SPRING_PROFILES_ACTIVE=dev

# Reload environment
source ~/.bashrc
```

### 4. Application Installation & Setup

#### Clone and Setup
```bash
# If starting fresh, the project structure should be:
# SiteMasterPro/
# ├── src/
# ├── pom.xml
# └── ...

# Navigate to project directory
cd SiteMasterPro

# Clean and install dependencies
mvn clean install -DskipTests

# Run database migrations (automatic on first run)
mvn flyway:migrate
```

#### Verify Dependencies Installation
```bash
# Check if all dependencies are resolved
mvn dependency:tree

# Compile the project
mvn compile

# Run tests (optional)
mvn test
```

### 5. Running the Application

#### Development Mode
```bash
# Method 1: Using Maven Spring Boot plugin
cd SiteMasterPro
mvn spring-boot:run

# Method 2: Using JAR file
mvn clean package -DskipTests
java -jar target/site-master-pro-1.0.0.jar

# Method 3: Using IDE
# Import as Maven project and run SiteMasterProApplication.java
```

#### Access the Application
- **URL**: `http://localhost:5000` (local) or your URL
- **Default Login**: 
  - Username: `admin`
  - Password: `admin123`

### 6. Default Test Users
After successful startup, you can login with these pre-configured users:

| Role | Username | Password | Email |
|------|----------|----------|-------|
| Super Admin | admin | admin123 | admin@sitemasterpro.com |
| CEO | ceo | admin123 | ceo@sitemasterpro.com |
| Accountant | accountant | admin123 | accountant@sitemasterpro.com |
| Store Keeper | storekeeper | admin123 | storekeeper@sitemasterpro.com |
| Site Manager | sitemanager | admin123 | sitemanager@sitemasterpro.com |
| Site Engineer | engineer | admin123 | engineer@sitemasterpro.com |
| Labor Head | laborhead | admin123 | laborhead@sitemasterpro.com |

## Production Deployment Guide

### 1. Pre-Deployment Preparation

#### Environment Variables for Production
```bash
# Required production environment variables
DATABASE_URL=jdbc:postgresql://your-production-db:5432/sitemasterpro
PGUSER=your_production_user
PGPASSWORD=your_very_secure_password
JWT_SECRET=your_256_bit_jwt_secret_key_minimum_32_characters
SPRING_PROFILES_ACTIVE=prod
```

#### Production Database Setup
```sql
-- Production PostgreSQL setup
CREATE DATABASE sitemasterpro_prod;
CREATE USER sitemasterpro_prod WITH PASSWORD 'very_secure_production_password';
GRANT ALL PRIVILEGES ON DATABASE sitemasterpro_prod TO sitemasterpro_prod;

-- Additional security configurations
ALTER USER sitemasterpro_prod SET search_path TO sitemasterpro_prod,public;
```

### 2. Building for Production

#### Create Production Build
```bash
cd SiteMasterPro

# Clean previous builds
mvn clean

# Create production package (skip tests for faster build)
mvn package -DskipTests -Pprod

# Or with tests (recommended for production)
mvn package

# Verify the JAR file
ls -la target/site-master-pro-1.0.0.jar
```

#### Production JAR Optimization
```bash
# Optional: Create optimized JAR with specific profile
mvn clean package -Dspring.profiles.active=prod -DskipTests

# Test the production build locally
java -Dspring.profiles.active=prod -jar target/site-master-pro-1.0.0.jar
```
#### Post-Deployment Verification
```bash
# Check application health
curl https://your-repl-name.your-username.repl.co/actuator/health

# Verify API endpoints
curl https://your-repl-name.your-username.repl.co/api/auth/test
```

### 4. Production Configuration Best Practices

#### Security Hardening
1. **JWT Security**:
   ```yaml
   # Use strong JWT secret (minimum 256 bits)
   app:
     jwt:
       secret: ${JWT_SECRET:your_very_long_and_secure_secret_key_here}
       expiration: 86400000 # 24 hours
   ```

2. **Database Security**:
   ```yaml
   spring:
     datasource:
       username: ${PGUSER}
       password: ${PGPASSWORD}
       # Enable SSL for production
       url: ${DATABASE_URL}?sslmode=require
   ```

3. **Application Security**:
   ```yaml
   # Disable debug features
   spring:
     jpa:
       show-sql: false
     thymeleaf:
       cache: true
   
   logging:
     level:
       com.sitemasterpro: INFO
       org.springframework.security: WARN
   ```

#### Performance Optimization
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 5. Monitoring & Maintenance

#### Health Checks & Monitoring
```bash
# Application health endpoint
curl /actuator/health

# Metrics endpoint
curl /actuator/metrics

# Application info
curl /actuator/info
```

#### Log Monitoring
```bash
# Check application logs
tail -f logs/site-master-pro.log

# Monitor database connections
grep "HikariPool" logs/site-master-pro.log
```

#### Backup Strategy
```bash
# Database backup (PostgreSQL)
pg_dump -h hostname -U username -d sitemasterpro_prod > backup_$(date +%Y%m%d_%H%M%S).sql

# Automated backup script
#!/bin/bash
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME > $BACKUP_DIR/sitemasterpro_$DATE.sql
```

## API Documentation

### Authentication Endpoints
```bash
# User login
POST /api/auth/signin
Content-Type: application/json
{
  "username": "admin",
  "password": "admin123"
}

# User registration (Admin only)
POST /api/auth/signup
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password",
  "fullName": "Full Name",
  "roles": ["SITE_MANAGER"]
}
```

### Project Management Endpoints
```bash
# Get all projects
GET /api/projects
Authorization: Bearer {jwt_token}

# Create new project
POST /api/projects
Authorization: Bearer {jwt_token}
Content-Type: application/json
{
  "name": "New Project",
  "description": "Project description",
  "budget": 1000000.00,
  "clientName": "Client Name"
}
```

### Financial Endpoints
```bash
# Project financial summary
GET /api/reports/project/{projectId}/financial
Authorization: Bearer {jwt_token}

# Budget tracking
GET /api/financials/budget/{projectId}
Authorization: Bearer {jwt_token}
```

## Troubleshooting Guide

### Common Issues & Solutions

#### 1. Application Won't Start
```bash
# Check Java version
java -version
# Should be 17 or higher

# Check Maven version
mvn -version
# Should be 3.6 or higher

# Clear Maven cache
mvn dependency:purge-local-repository

# Rebuild
mvn clean install -DskipTests
```

#### 2. Database Connection Errors
```bash
# Test database connectivity
psql -h localhost -U postgres -d sitemasterpro

# Check environment variables
echo $DATABASE_URL
echo $PGUSER
echo $PGPASSWORD

# Verify database exists
psql -h localhost -U postgres -c "\l" | grep sitemasterpro
```

#### 3. JWT Token Issues
```bash
# Verify JWT secret is set
echo $JWT_SECRET
# Should be at least 32 characters

# Check token format in browser dev tools
# Should be: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### 4. Port Already in Use
```bash
# Find process using port 5000
sudo lsof -i :5000

# Kill process if needed
sudo kill -9 <PID>

# Or change port in application.yml
server:
  port: 8080
```

#### 5. Maven Dependencies Issues
```bash
# Clear Maven repository
rm -rf ~/.m2/repository

# Re-download dependencies
mvn clean install -U

# Check for dependency conflicts
mvn dependency:tree
```

### Performance Issues
```bash
# Monitor memory usage
java -Xms512m -Xmx2g -jar target/site-master-pro-1.0.0.jar

# Enable JVM monitoring
java -XX:+UseG1GC -XX:+UseStringDeduplication -jar target/site-master-pro-1.0.0.jar

# Database performance
# Add indexes for frequently queried columns
# Monitor slow queries in PostgreSQL logs
```

## Development Guidelines

### Code Structure
```
SiteMasterPro/
├── src/main/java/com/sitemasterpro/
│   ├── config/           # Spring configuration classes
│   ├── controller/       # REST API controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── enums/           # Application enums
│   ├── exception/       # Custom exceptions
│   ├── repository/      # Data access layer
│   ├── security/        # Security configurations
│   ├── service/         # Business logic layer
│   ├── util/            # Utility classes
│   └── websocket/       # WebSocket handlers
└── src/main/resources/
    ├── db/migration/    # Flyway database migrations
    ├── static/          # Static web assets (CSS, JS, images)
    ├── templates/       # Thymeleaf templates
    └── application.yml  # Application configuration
```

### Adding New Features
1. Create entity classes in `entity/` package
2. Add repository interfaces in `repository/` package
3. Implement business logic in `service/` package
4. Create REST controllers in `controller/` package
5. Add database migrations in `db/migration/`
6. Create frontend templates in `templates/`

## Support & Documentation

### Additional Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

### Getting Help
1. Check application logs for error details
2. Verify environment configuration
3. Test with default credentials
4. Check database connectivity
5. Review security configurations

## License
This project is proprietary software designed for civil construction project management.

---