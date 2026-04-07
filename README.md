# 🌱 Smart Agriculture System (IoT + AI Powered)

An intelligent agriculture system that combines IoT sensor data, weather APIs, and AI (Spring AI) to provide real-time irrigation alerts, crop recommendations, and disease insights.

---

## ⚡ Problem Statement

Traditional farming relies on manual decisions, leading to:

* Water wastage
* Crop damage due to late disease detection
* Poor crop selection

---

## 🧠 Solution

This system integrates:

* IoT sensor data (soil moisture)
* Weather API (temperature & humidity)
* AI-based recommendations (Spring AI)

to provide **automated, data-driven farming decisions**.

---

## 🏗️ System Architecture

Client (ESP32 / Frontend)
↓
REST API Layer (Spring Boot Controllers)
↓
Service Layer (Business Logic)
↓

* Sensor Processing
* Crop Recommendation (AI Service)
* Disease Detection
  ↓
  Data Layer (JPA + PostgreSQL)
  ↓
  External Integrations:
* Weather API
* AI Engine (Spring AI)
* Email Notification System

---

## 🚀 Core Features

### 🌡️ Irrigation Monitoring

* Real-time soil moisture tracking
* Status classification: OK / WARNING / CRITICAL
* Automated email alerts with cooldown

---

### 🌿 Crop Disease Detection

* Upload crop images to detect diseases
* Returns disease name + confidence
* AI-generated explanation & treatment suggestions

---

### 🌾 AI Crop Recommendation

* Uses environmental data:

  * Soil moisture
  * Temperature
  * Humidity
* AI suggests best crops for given conditions

---

### 🤖 AI Integration (Spring AI)

* Intelligent crop suggestions using LLM
* Disease explanation in simple language
* Context-aware agricultural insights

---

## 🛠️ Tech Stack

* Backend: Java, Spring Boot
* AI: Spring AI (OpenAI API)
* Database: PostgreSQL / MySQL
* APIs: Weather API
* Tools: Postman, Git

---

## 🔌 API Endpoints

### 📍 Sensor Data

POST /api/sensor/data

### 📍 Latest Data

GET /api/sensor/latest

### 📍 Crop Recommendation (AI)

POST /api/crop/recommend

### 📍 Disease Detection

POST /api/disease/detect

---

## 📊 Sample Request

```json
{
  "soilMoisture": 22.5,
  "city": "Delhi",
  "deviceId": "ESP32-FARM-01"
}
```

---

## 📊 Sample Response

```json
{
  "deviceId": "ESP32-FARM-01",
  "city": "Delhi",
  "status": "CRITICAL",
  "soilMoisture": 22.5,
  "temperature": 30,
  "humidity": 65,
  "emailStatus": "Sent",
  "timestamp": "2026-04-06T01:52:00"
}
```

---

## 🤖 AI Implementation Details

* Integrated Spring AI for LLM-based decision support
* Uses dynamic prompt engineering for:

  * Crop recommendations based on environmental conditions
  * Disease explanation with actionable treatment guidance
* Combines rule-based validation with AI-generated insights
* Produces human-readable recommendations for real-world usability

---

## 🗄️ Database Design

* **SensorData**

  * deviceId, soilMoisture, temperature, humidity, timestamp

* **CropRecommendation**

  * input conditions, AI response, createdAt

* **DiseaseDetection**

  * imagePath, detectedDisease, confidence, AI suggestions

* Optimized for real-time sensor data storage and retrieval

---

## ⚙️ Backend Capabilities

* RESTful API design with layered architecture
* Input validation and structured error handling
* Pagination support for historical sensor data
* Modular service-layer architecture for scalability

---

## 🌍 Real-World Impact

* Automates irrigation decisions, reducing manual effort
* Enables early disease detection to minimize crop loss
* Provides AI-driven crop recommendations based on conditions
* Converts raw IoT data into actionable farming decisions

---

## 📌 Key Engineering Decisions

* Used Spring Boot for scalable backend architecture
* Integrated AI for decision-making instead of static logic
* Designed system to handle real-time IoT data flow
* Applied separation of concerns using service-layer design

---

## 🚀 Future Enhancements

* Deploy on AWS / Render
* Add Redis caching for real-time data
* Use Kafka for asynchronous processing
* Integrate real IoT sensors (ESP32 live feed)

---

## 👨‍💻 Author

Siddharth 
Backend Engineer | Java • Spring Boot • APIs • AI Integration