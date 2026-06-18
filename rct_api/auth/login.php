<?php
header("Content-Type: application/json");
include "../config/db.php";

$data = json_decode(file_get_contents("php://input"), true);

if (!$data) {
    echo json_encode(["status" => "error", "message" => "No input"]);
    exit;
}

$email = $conn->real_escape_string($data["email"]);
$pass  = $data["password"];

$sql    = "SELECT * FROM users WHERE email='$email'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    if (password_verify($pass, $user["password"]) || 
        $pass === $user["password"]) {
        echo json_encode([
            "status" => "success",
            "role"   => $user["role"],
            "name"   => $user["name"],
            "id"     => $user["id"]
        ]);
    } else {
        echo json_encode([
            "status"  => "error",
            "message" => "Invalid credentials"
        ]);
    }
} else {
    echo json_encode([
        "status"  => "error",
        "message" => "Invalid credentials"
    ]);
}
?>