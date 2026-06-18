<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "rct_app";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode(["status" => "error",
                     "message" => $conn->connect_error]));
}
?>