package com.rct.app.api;

public class ApiConfig {
    public static final String BASE_URL = "http://10.60.127.221/rct_api/";


    // Auth
    public static final String LOGIN           = BASE_URL + "auth/login.php";
    public static final String REGISTER        = BASE_URL + "auth/register.php";
    public static final String FORGOT_PASSWORD = BASE_URL + "auth/forgot_password.php";

    // Password Reset (admin direct reset)
    public static final String RESET_PASSWORD = BASE_URL + "admin/reset_password.php";

    // Admin - existing
    public static final String GET_PATIENTS    = BASE_URL + "admin/get_patients.php";
    public static final String GET_SCORES      = BASE_URL + "admin/get_scores.php";
    public static final String GET_CONSENT     = BASE_URL + "admin/get_consent.php";
    public static final String GET_ATTENDANCE  = BASE_URL + "admin/get_attendance.php";
    public static final String EXPORT_DATA     = BASE_URL + "admin/export.php";
    public static final String SAVE_SCORE      = BASE_URL + "admin/save_score.php";
    public static final String SAVE_CONSENT    = BASE_URL + "admin/save_consent.php";
    public static final String SAVE_ATTENDANCE = BASE_URL + "admin/save_attendance.php";

    // Admin - new
    public static final String GET_PROCEDURES   = BASE_URL + "admin/get_procedures.php";
    public static final String ASSIGN_PROCEDURE = BASE_URL + "admin/assign_procedure.php";
    public static final String GET_OUTCOMES     = BASE_URL + "admin/get_outcomes.php";
    public static final String GET_COMPARATOR   = BASE_URL + "admin/get_comparator.php";

    // Patient - new
    public static final String GET_MY_PROCEDURE      = BASE_URL + "patient/get_my_procedure.php";
    public static final String SAVE_KNOWLEDGE        = BASE_URL + "patient/save_knowledge.php";
    public static final String SAVE_ANXIETY          = BASE_URL + "patient/save_anxiety.php";
    public static final String SAVE_SATISFACTION     = BASE_URL + "patient/save_satisfaction.php";
    public static final String SAVE_POSTOP           = BASE_URL + "patient/save_postop.php";
    public static final String SAVE_BASELINE         = BASE_URL + "patient/save_baseline.php";
    public static final String GET_PATIENT_PROCEDURE = BASE_URL + "admin/get_patient_procedure.php";
    public static final String GET_EDUCATION_CONTENT = BASE_URL + "admin/get_education_content.php";
}