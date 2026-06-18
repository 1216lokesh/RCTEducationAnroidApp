<?php
header("Content-Type: application/json");
include "../config/db.php";

$data    = json_decode(file_get_contents("php://input"), true);

if (!$data) {
    echo json_encode(["status" => "error", "message" => "No input"]);
    exit;
}

$user_id = $conn->real_escape_string($data["user_id"]);
$quiz    = $conn->real_escape_string($data["quiz"]);
$score   = $conn->real_escape_string($data["score"]);

$check = $conn->query(
    "SELECT id FROM scores WHERE user_id='$user_id'"
);

if ($check->num_rows > 0) {
    $sql = "UPDATE scores SET $quiz='$score' 
            WHERE user_id='$user_id'";
} else {
    $sql = "INSERT INTO scores (user_id, $quiz) 
            VALUES ('$user_id', '$score')";
}

if ($conn->query($sql)) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error",
                      "message" => $conn->error]);
}
?>