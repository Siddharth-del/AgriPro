# Smart-Agriculture-system
**Overview:**

Smart Agriculture System is a role-based web application designed to assist farmers with crop selection, plant disease detection, and intelligent agricultural recommendations.
The system combines Machine Learning and Large Language Models (LLMs) to provide predictive analytics along with clear, human-readable explanations for agricultural decisions.

**Key Features**

Role-Based Authentication (Farmer / Admin) using JWT

Crop Recommendation based on soil and environmental parameters

Image-based Plant Disease Detection

AI-powered Explanation using Spring AI (LLM)

Fertilizer and Pesticide Suggestions

Automated Email Notifications for onboarding and alerts

Global Exception Handling with structured API responses

**AI Integration:**

1. Machine Learning Model

2. Python-trained ML model for crop prediction

3. Predicts optimal crops using soil moisture, temperature, humidity, etc.

4. Integrated with Spring Boot backend

5. LLM-Based Explanation (Spring AI)

6. Uses Spring AI abstraction layer

7. Generates contextual explanations for:

Why a crop is recommended

Causes of detected plant diseases

Prevention and treatment methods

Structured prompt templates used to improve output consistency

**System Architecture:**

Layered Architecture:

**Controller** → **Service** → **Repository** → **Database**→ **ML Model**→ **Spring AI (LLM)**

**JWT-based stateless authentication:**

1.Custom UserDetails implementation for role management

2.Global exception handling using @ControllerAdvice

**Tech Stack:**

1. Java

2. Spring Boot

3. Spring Security (JWT)

4. Spring AI

5. PostgreSQL 

6. Python (ML Model)

7.REST APIs

**How to Run Locally**
1. Clone the repository
   
git clone 

3. Configure Database
Update application.properties with your database credentials.

4. Run the Application
mvn spring-boot:run

**Authentication Flow:**

User registers (Farmer/Admin)

JWT token generated on login

Token required in Authorization header for protected endpoints

Role-based access enforced via Spring Security

**Future Enhancements:**

IoT sensor integration for real-time soil monitoring.

Yield prediction using historical data.

Automated irrigation decision system.

Cloud deployment.# Smart-Agriculture-System-backend
