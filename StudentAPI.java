package com.example.sms;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;

public class StudentAPI {
    private static final StudentDAO dao = new StudentDAO();
    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        // Create context handlers
        server.createContext("/api/students", new StudentHandler());
        server.createContext("/", new RootHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Student Management API running on http://localhost:" + PORT);
        System.out.println("Frontend: http://localhost:" + PORT + "/index.html");
    }

    static class StudentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            try {
                switch (method) {
                    case "GET":
                        handleGet(exchange, path, query);
                        break;
                    case "POST":
                        handlePost(exchange);
                        break;
                    case "PUT":
                        handlePut(exchange, path);
                        break;
                    case "DELETE":
                        handleDelete(exchange, path);
                        break;
                    default:
                        sendError(exchange, 405, "Method not allowed");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleGet(HttpExchange exchange, String path, String query) throws IOException, SQLException {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                Student student = dao.getStudentById(id);
                if (student != null) {
                    String json = studentToJson(student);
                    sendResponse(exchange, 200, json);
                } else {
                    sendError(exchange, 404, "Student not found");
                }
            } else {
                List<Student> students = dao.getAllStudents();
                StringBuilder jsonArray = new StringBuilder("[");
                for (int i = 0; i < students.size(); i++) {
                    if (i > 0) jsonArray.append(",");
                    jsonArray.append(studentToJson(students.get(i)));
                }
                jsonArray.append("]");
                sendResponse(exchange, 200, jsonArray.toString());
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException, SQLException {
            String body = readBody(exchange);
            String[] parts = parseJson(body);
            
            Student student = new Student();
            student.setName(parts[0]);
            student.setEmail(parts[1]);
            student.setAge(Integer.parseInt(parts[2]));

            dao.addStudent(student);
            sendResponse(exchange, 201, "{\"message\": \"Student added successfully\"}");
        }

        private void handlePut(HttpExchange exchange, String path) throws IOException, SQLException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            String body = readBody(exchange);
            String[] parts = parseJson(body);

            Student student = new Student();
            student.setId(id);
            student.setName(parts[0]);
            student.setEmail(parts[1]);
            student.setAge(Integer.parseInt(parts[2]));

            dao.updateStudent(student);
            sendResponse(exchange, 200, "{\"message\": \"Student updated successfully\"}");
        }

        private void handleDelete(HttpExchange exchange, String path) throws IOException, SQLException {
            int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            dao.deleteStudent(id);
            sendResponse(exchange, 200, "{\"message\": \"Student deleted successfully\"}");
        }

        private String studentToJson(Student s) {
            return "{\"id\":" + s.getId() + ",\"name\":\"" + escapeJson(s.getName()) + 
                   "\",\"email\":\"" + escapeJson(s.getEmail()) + "\",\"age\":" + s.getAge() + "}";
        }

        private String[] parseJson(String json) {
            String[] result = new String[3];
            result[0] = extractValue(json, "name");
            result[1] = extractValue(json, "email");
            result[2] = extractValue(json, "age");
            return result;
        }

        private String extractValue(String json, String key) {
            String searchKey = "\"" + key + "\":";
            int startIdx = json.indexOf(searchKey);
            if (startIdx == -1) return "";
            
            startIdx += searchKey.length();
            char firstChar = json.charAt(startIdx);
            
            if (firstChar == '"') {
                int endIdx = json.indexOf('"', startIdx + 1);
                return json.substring(startIdx + 1, endIdx);
            } else {
                int endIdx = json.indexOf(',', startIdx);
                if (endIdx == -1) endIdx = json.indexOf('}', startIdx);
                return json.substring(startIdx, endIdx).trim();
            }
        }

        private String escapeJson(String str) {
            return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
        }

        private String readBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }

        private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
            exchange.sendResponseHeaders(code, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void sendError(HttpExchange exchange, int code, String message) throws IOException {
            String error = "{\"error\":\"" + escapeJson(message) + "\"}";
            sendResponse(exchange, code, error);
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "text/html");

            if (exchange.getRequestURI().getPath().equals("/")) {
                String html = getIndexHTML();
                exchange.sendResponseHeaders(200, html.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.getBytes());
                os.close();
            } else if (exchange.getRequestURI().getPath().equals("/index.html")) {
                String html = getIndexHTML();
                exchange.sendResponseHeaders(200, html.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            }
        }

        private String getIndexHTML() {
            return "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Student Management System</title>\n" +
                    "    <style>\n" +
                    "        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n" +
                    "        .container { max-width: 1000px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n" +
                    "        h1 { color: #333; text-align: center; }\n" +
                    "        .form-group { margin-bottom: 15px; }\n" +
                    "        label { display: block; margin-bottom: 5px; font-weight: bold; color: #555; }\n" +
                    "        input, button { padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }\n" +
                    "        input { width: 100%; box-sizing: border-box; }\n" +
                    "        button { background-color: #4CAF50; color: white; cursor: pointer; margin-right: 5px; }\n" +
                    "        button:hover { background-color: #45a049; }\n" +
                    "        button.delete { background-color: #f44336; }\n" +
                    "        button.delete:hover { background-color: #da190b; }\n" +
                    "        button.clear { background-color: #999; }\n" +
                    "        button.clear:hover { background-color: #777; }\n" +
                    "        table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n" +
                    "        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n" +
                    "        th { background-color: #4CAF50; color: white; }\n" +
                    "        tr:nth-child(even) { background-color: #f9f9f9; }\n" +
                    "        tr:hover { background-color: #f0f0f0; }\n" +
                    "        .message { margin-top: 10px; padding: 10px; border-radius: 4px; display: none; }\n" +
                    "        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }\n" +
                    "        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }\n" +
                    "        .button-group { margin-top: 15px; text-align: center; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <h1>📚 Student Management System</h1>\n" +
                    "        \n" +
                    "        <div id=\"message\" class=\"message\"></div>\n" +
                    "        \n" +
                    "        <div style=\"background-color: #f9f9f9; padding: 15px; border-radius: 4px; margin-bottom: 20px;\">\n" +
                    "            <h2>Add/Update Student</h2>\n" +
                    "            <div class=\"form-group\">\n" +
                    "                <label>ID (Leave empty for new students):</label>\n" +
                    "                <input type=\"number\" id=\"id\" placeholder=\"ID\">\n" +
                    "            </div>\n" +
                    "            <div class=\"form-group\">\n" +
                    "                <label>Name:</label>\n" +
                    "                <input type=\"text\" id=\"name\" placeholder=\"Student Name\" required>\n" +
                    "            </div>\n" +
                    "            <div class=\"form-group\">\n" +
                    "                <label>Email:</label>\n" +
                    "                <input type=\"email\" id=\"email\" placeholder=\"student@example.com\" required>\n" +
                    "            </div>\n" +
                    "            <div class=\"form-group\">\n" +
                    "                <label>Age:</label>\n" +
                    "                <input type=\"number\" id=\"age\" placeholder=\"Age\" min=\"1\" max=\"100\" required>\n" +
                    "            </div>\n" +
                    "            <div class=\"button-group\">\n" +
                    "                <button onclick=\"addStudent()\">Add Student</button>\n" +
                    "                <button onclick=\"updateStudent()\">Update Student</button>\n" +
                    "                <button onclick=\"deleteStudent()\" class=\"delete\">Delete Student</button>\n" +
                    "                <button onclick=\"clearForm()\" class=\"clear\">Clear</button>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "        \n" +
                    "        <h2>All Students</h2>\n" +
                    "        <button onclick=\"loadStudents()\" style=\"margin-bottom: 10px;\">🔄 Refresh</button>\n" +
                    "        <table id=\"studentTable\">\n" +
                    "            <thead>\n" +
                    "                <tr>\n" +
                    "                    <th>ID</th>\n" +
                    "                    <th>Name</th>\n" +
                    "                    <th>Email</th>\n" +
                    "                    <th>Age</th>\n" +
                    "                </tr>\n" +
                    "            </thead>\n" +
                    "            <tbody id=\"tableBody\">\n" +
                    "            </tbody>\n" +
                    "        </table>\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <script>\n" +
                    "        const API_URL = 'http://localhost:8081/api/students';\n" +
                    "        function showMessage(msg, type) {\n" +
                    "            const msgEl = document.getElementById('message');\n" +
                    "            msgEl.textContent = msg;\n" +
                    "            msgEl.className = 'message ' + type;\n" +
                    "            msgEl.style.display = 'block';\n" +
                    "            setTimeout(() => msgEl.style.display = 'none', 3000);\n" +
                    "        }\n" +
                    "\n" +
                    "        function loadStudents() {\n" +
                    "            fetch(API_URL)\n" +
                    "                .then(res => res.json())\n" +
                    "                .then(students => {\n" +
                    "                    const tbody = document.getElementById('tableBody');\n" +
                    "                    tbody.innerHTML = '';\n" +
                    "                    students.forEach(s => {\n" +
                    "                        const row = `<tr onclick=\"selectStudent(${s.id}, '${s.name}', '${s.email}', ${s.age})\" style=\"cursor: pointer;\">\n" +
                    "                            <td>${s.id}</td>\n" +
                    "                            <td>${s.name}</td>\n" +
                    "                            <td>${s.email}</td>\n" +
                    "                            <td>${s.age}</td>\n" +
                    "                        </tr>`;\n" +
                    "                        tbody.innerHTML += row;\n" +
                    "                    });\n" +
                    "                })\n" +
                    "                .catch(err => showMessage('Error loading students: ' + err, 'error'));\n" +
                    "        }\n" +
                    "\n" +
                    "        function selectStudent(id, name, email, age) {\n" +
                    "            document.getElementById('id').value = id;\n" +
                    "            document.getElementById('name').value = name;\n" +
                    "            document.getElementById('email').value = email;\n" +
                    "            document.getElementById('age').value = age;\n" +
                    "        }\n" +
                    "\n" +
                    "        function addStudent() {\n" +
                    "            const name = document.getElementById('name').value;\n" +
                    "            const email = document.getElementById('email').value;\n" +
                    "            const age = document.getElementById('age').value;\n" +
                    "\n" +
                    "            if (!name || !email || !age) {\n" +
                    "                showMessage('Please fill all fields!', 'error');\n" +
                    "                return;\n" +
                    "            }\n" +
                    "\n" +
                    "            fetch(API_URL, {\n" +
                    "                method: 'POST',\n" +
                    "                headers: { 'Content-Type': 'application/json' },\n" +
                    "                body: JSON.stringify({ name, email, age: parseInt(age) })\n" +
                    "            })\n" +
                    "            .then(res => res.json())\n" +
                    "            .then(data => {\n" +
                    "                showMessage('Student added successfully!', 'success');\n" +
                    "                clearForm();\n" +
                    "                loadStudents();\n" +
                    "            })\n" +
                    "            .catch(err => showMessage('Error: ' + err, 'error'));\n" +
                    "        }\n" +
                    "\n" +
                    "        function updateStudent() {\n" +
                    "            const id = document.getElementById('id').value;\n" +
                    "            const name = document.getElementById('name').value;\n" +
                    "            const email = document.getElementById('email').value;\n" +
                    "            const age = document.getElementById('age').value;\n" +
                    "\n" +
                    "            if (!id || !name || !email || !age) {\n" +
                    "                showMessage('Please fill all fields including ID!', 'error');\n" +
                    "                return;\n" +
                    "            }\n" +
                    "\n" +
                    "            fetch(API_URL + '/' + id, {\n" +
                    "                method: 'PUT',\n" +
                    "                headers: { 'Content-Type': 'application/json' },\n" +
                    "                body: JSON.stringify({ name, email, age: parseInt(age) })\n" +
                    "            })\n" +
                    "            .then(res => res.json())\n" +
                    "            .then(data => {\n" +
                    "                showMessage('Student updated successfully!', 'success');\n" +
                    "                clearForm();\n" +
                    "                loadStudents();\n" +
                    "            })\n" +
                    "            .catch(err => showMessage('Error: ' + err, 'error'));\n" +
                    "        }\n" +
                    "\n" +
                    "        function deleteStudent() {\n" +
                    "            const id = document.getElementById('id').value;\n" +
                    "            if (!id) {\n" +
                    "                showMessage('Please select a student to delete!', 'error');\n" +
                    "                return;\n" +
                    "            }\n" +
                    "\n" +
                    "            if (!confirm('Are you sure you want to delete this student?')) return;\n" +
                    "\n" +
                    "            fetch(API_URL + '/' + id, { method: 'DELETE' })\n" +
                    "            .then(res => res.json())\n" +
                    "            .then(data => {\n" +
                    "                showMessage('Student deleted successfully!', 'success');\n" +
                    "                clearForm();\n" +
                    "                loadStudents();\n" +
                    "            })\n" +
                    "            .catch(err => showMessage('Error: ' + err, 'error'));\n" +
                    "        }\n" +
                    "\n" +
                    "        function clearForm() {\n" +
                    "            document.getElementById('id').value = '';\n" +
                    "            document.getElementById('name').value = '';\n" +
                    "            document.getElementById('email').value = '';\n" +
                    "            document.getElementById('age').value = '';\n" +
                    "        }\n" +
                    "\n" +
                    "        // Load students on page load\n" +
                    "        loadStudents();\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }
}
