<?php
header("Content-Type: application/json");
include "../config/db.php";

$data  = json_decode(file_get_contents("php://input"), true);
$name  = $conn->real_escape_string($data["name"]);
$email = $conn->real_escape_string($data["email"]);
$pass  = password_hash($data["password"], PASSWORD_DEFAULT);
$phone = $conn->real_escape_string($data["phone"]);
$role  = "patient";

$check = $conn->query("SELECT id FROM users WHERE email='$email'");
if ($check->num_rows > 0) {
    echo json_encode(["status" => "error", 
                      "message" => "Email already exists"]);
    exit;
}

$sql = "INSERT INTO users (name, email, password, phone, role) 
        VALUES ('$name','$email','$pass','$phone','$role')";

if ($conn->query($sql)) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error", 
                      "message" => "Registration failed"]);
}
?>