# Student Management System - Web Application

A complete full-stack application with **Java REST API backend** and **embedded HTML/JavaScript frontend**.

## 🎯 What You Have

### ✅ **REST API Server** (Port 8080)
- Built with Java's `HttpServer` (no external dependencies)
- JSON-based CRUD endpoints
- CORS-enabled for cross-origin requests
- Embedded HTML/JavaScript UI

### ✅ **Web Frontend**
- Dynamic student management interface
- Add, Update, Delete, View operations
- Real-time success/error messages
- Table view of all students
- Click rows to select for editing

### ✅ **Multiple Interfaces**
1. **Web UI** (HTML/JS) - Primary interface
2. **Swing GUI** - Desktop application
3. **CLI** - Command-line interface
4. **REST API** - For programmatic access

### ✅ **Database**
- MySQL 8.0 backend
- `studentdb` database
- `students` table with 8 sample records

---

## 🚀 How to Run

### Step 1: Compile
```bash
cd C:\SMS\SMS
javac -cp "lib/*" -d . src/com/example/sms/*.java
```

### Step 2: Start API Server
```bash
java -cp "lib\mysql-connector-j-9.5.0.jar;." com.example.sms.StudentAPI
```

### Step 3: Open Browser
```
http://localhost:8080/index.html
```

---

## 📡 REST API Endpoints

All endpoints are available at: `http://localhost:8080/api/students`

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/students` | Get all students |
| GET | `/api/students?id=1` | Get specific student |
| POST | `/api/students` | Add new student |
| PUT | `/api/students/{id}` | Update student |
| DELETE | `/api/students/{id}` | Delete student |

### Example API Calls

**Get all students:**
```bash
curl http://localhost:8080/api/students
```

**Add student:**
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","age":20}'
```

**Update student:**
```bash
curl -X PUT http://localhost:8080/api/students/9 \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane@example.com","age":21}'
```

**Delete student:**
```bash
curl -X DELETE http://localhost:8080/api/students/9
```

---

## 🖥️ Frontend Usage

### Add a Student
1. Fill in Name, Email, Age fields
2. Click "Add Student" button
3. Table refreshes automatically

### Update a Student
1. Click any row in the table to select it
2. Form fields populate with student data
3. Modify the values
4. Click "Update Student" button

### Delete a Student
1. Click a row to select it
2. Click "Delete Student" button
3. Confirm deletion
4. Student is removed from database

### Refresh Data
1. Click "Refresh" button to reload from database
2. Clear Form to reset input fields

---

## 📊 Current Database

**8 Students:**
1. Raj Kumar - raj@example.com - Age 20
2. Priya Singh - priya@example.com - Age 21
3. Amit Patel - amit@example.com - Age 19
4. Sneha Sharma - sneha@example.com - Age 22
5. Vikram Verma - vikram@example.com - Age 20
6. Divya Nair - divya@example.com - Age 23
7. suresh - suresh@gmail.com - Age 21
8. dhiyana - dhiyana@gmail.com - Age 21

---

## 🎯 Project Files

```
SMS/
├── lib/
│   └── mysql-connector-j-9.5.0.jar
├── src/com/example/sms/
│   ├── Student.java                 # Model class
│   ├── StudentDAO.java              # Database operations
│   ├── DBConnection.java            # MySQL connection
│   ├── StudentAPI.java              # REST API + Frontend
│   ├── StudentManagementSystem.java  # CLI app
│   ├── StudentManagementUI.java      # Swing GUI
│   └── TestDBConnection.java         # Test
├── test/com/example/sms/
│   └── TestDAO.java
├── README.md                         # Original docs
└── API_DOCUMENTATION.md              # This file
```

---

## 🔧 Technical Details

### StudentAPI.java Features
- **StudentHandler** - Handles /api/students routes
  - GET: Fetch students
  - POST: Add student
  - PUT: Update student
  - DELETE: Delete student
  - OPTIONS: CORS preflight
  
- **RootHandler** - Serves HTML UI
  - GET /: Serves index.html
  - GET /index.html: Serves index.html
  - Built-in JavaScript for CRUD operations

### CORS Headers
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

### Database Connection
- **Host:** localhost
- **Port:** 3306
- **Database:** studentdb
- **User:** root
- **Password:** Suresh@1234

---

## ⚙️ Troubleshooting

### Port 8080 Already in Use
```powershell
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### MySQL Connection Failed
```powershell
Get-Service MySQL80  # Check if running
```

### API Not Responding
1. Check Java process is running
2. Verify MySQL service is active
3. Check firewall settings for port 8080

---

## 💾 Database Setup

The database and table are already created. To recreate:

```sql
CREATE DATABASE IF NOT EXISTS studentdb;
USE studentdb;

CREATE TABLE IF NOT EXISTS students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  age INT NOT NULL
);
```

---

## 🎓 Running Other Interfaces

### Swing GUI
```bash
java -cp "lib/*;." com.example.sms.StudentManagementUI
```

### CLI Application
```bash
java -cp "lib/*;." com.example.sms.StudentManagementSystem
```

---

## ✨ Features Summary

✅ Full REST API with JSON responses  
✅ Embedded web UI (no separate frontend needed)  
✅ CRUD operations for students  
✅ Real-time database synchronization  
✅ Error handling and validation  
✅ CORS support  
✅ No external dependencies (uses Java built-in HttpServer)  
✅ MySQL database integration  
✅ Multiple interface options (Web, Desktop, CLI)  

---

## 📝 Notes

- The REST API is fully functional and tested
- Frontend automatically loads student data on startup
- All operations are immediately reflected in the database
- The API is ready for integration with mobile apps or other frontends
