<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
include "../config/db.php";

$result = $conn->query(
    "SELECT id, name, email, phone 
     FROM users 
     WHERE role='patient'"
);

$patients = [];
while ($row = $result->fetch_assoc()) {
    $patients[] = [
        "id"    => (int)$row["id"],
        "name"  => $row["name"],
        "email" => $row["email"],
        "phone" => $row["phone"]
    ];
}

echo json_encode($patients);
?>