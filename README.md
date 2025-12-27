# Student Management System (Java + MySQL + JDBC)

Create database and table:

```sql
CREATE DATABASE studentdb;
USE studentdb;
CREATE TABLE students (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  email VARCHAR(100),
  age INT
);
```

Update DBConnection.java with your MySQL username/password.
Compile with the MySQL JDBC driver on the classpath.
