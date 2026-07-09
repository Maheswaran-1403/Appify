package com.example.data.model

enum class Department(val displayName: String, val icon: String, val colorHex: Long) {
    CORE_MANAGEMENT("Core Management", "🧠", 0xFF00FFCC), // Neon cyan
    DATA("Data & Analytics", "📊", 0xFF88CCFF),             // Light blue
    ML("Machine Learning", "🤖", 0xFFFF99FF),              // Neon purple/pink
    SOFTWARE("Software Engineering", "💻", 0xFFFFFF99),     // Light yellow
    QUALITY("Quality Assurance", "🧪", 0xFFFF9999),          // Coral/Red
    DEVOPS("DevOps & Infra", "☁️", 0xFF99FF99),              // Pale green
    DOCUMENTATION("Technical Docs", "📄", 0xFFE0E0E0),      // Gray
    USER("User Relations", "💬", 0xFFFFCC80)              // Orange
}

enum class AgentType(val displayName: String, val dept: Department, val role: String) {
    // Core Management
    SUPERVISOR("Supervisor Agent", Department.CORE_MANAGEMENT, "Orchestrates the entire team, directs focus and validates final delivery."),
    PLANNER("Planner Agent", Department.CORE_MANAGEMENT, "Deconstructs prompts into sequential execution stages and sets architectural path."),
    TASK_MANAGER("Task Manager Agent", Department.CORE_MANAGEMENT, "Tracks task state, assigns workloads, and enforces operational deadlines."),
    MEMORY("Memory Agent", Department.CORE_MANAGEMENT, "Maintains session context, project history, and past code patterns."),
    RETRY_RECOVERY("Retry & Recovery Agent", Department.CORE_MANAGEMENT, "Monitors failure alerts, diagnoses errors, and rewires execution chains."),

    // Data
    REQUIREMENT_ANALYSIS("Requirement Analysis Agent", Department.DATA, "Translates open-ended ideas into strict specifications and schemas."),
    DATASET_SEARCH("Dataset Search Agent", Department.DATA, "Searches public repositories, APIs, and registries for relevant starting datasets."),
    DATASET_VALIDATION("Dataset Validation Agent", Department.DATA, "Checks training and seed data for integrity, biases, and schema errors."),
    DATA_CLEANING("Data Cleaning Agent", Department.DATA, "Resolves missing values, formats anomalies, and prunes dataset noise."),
    EDA("EDA Agent", Department.DATA, "Performs Exploratory Data Analysis, drawing correlations and distribution profiles."),
    FEATURE_ENGINEERING("Feature Engineering Agent", Department.DATA, "Synthesizes mathematical input representations and normalizations."),
    DATA_AUGMENTATION("Data Augmentation Agent", Department.DATA, "Generates synthetic records and applies visual/textual transformations to balance data."),

    // ML
    MODEL_SELECTION("Model Selection Agent", Department.ML, "Evaluates model architectures and selects the optical model size and type."),
    HYPERPARAMETER_TUNING("Hyperparameter Tuning Agent", Department.ML, "Executes multi-axis sweep parameters (learning rates, batch sizes, epochs)."),
    TRAINING("Training Agent", Department.ML, "Spawns distributed computational workers to optimize weights against cost functions."),
    EVALUATION("Evaluation Agent", Department.ML, "Computes confusion matrices, F1-scores, loss metrics, and convergence speeds."),
    EXPLAINABILITY("Explainability Agent", Department.ML, "Generates SHAP/LIME diagrams to explain feature attributions and decisions."),
    MODEL_REGISTRY("Model Registry Agent", Department.ML, "Archives optimized weights, model cards, and registers API entry points."),

    // Software
    BACKEND_GENERATION("Backend Generation Agent", Department.SOFTWARE, "Generates secure server endpoints, APIs, and database controllers."),
    FRONTEND_GENERATION("Frontend Generation Agent", Department.SOFTWARE, "Designs interactive client-side interfaces, buttons, layouts, and charts."),
    DATABASE("Database Agent", Department.SOFTWARE, "Drafts migrations, triggers, relational schemas, and query optimization layers."),
    API_INTEGRATION("API Integration Agent", Department.SOFTWARE, "Connects third-party services, webhooks, auth gateways, and email alerts."),

    // Quality
    TESTING("Testing Agent", Department.QUALITY, "Drafts robust unit tests, component tests, and End-to-End browser assertions."),
    BUG_FIX("Bug Fix Agent", Department.QUALITY, "Reviews failure traces, matches errors against code, and applies surgical bug patches."),
    SECURITY_SCAN("Security Scan Agent", Department.QUALITY, "Audits source files and configs for package exploits, OWASP breaches, and leaked keys."),
    PERFORMANCE_OPT("Performance Optimization Agent", Department.QUALITY, "Analyzes memory profiles, asset sizes, load latency, and implements caching."),

    // DevOps
    DOCKER("Docker Agent", Department.DEVOPS, "Generates lean multi-stage Dockerfiles and compose orchestration manifests."),
    CICD("CI/CD Agent", Department.DEVOPS, "Configs GitHub Actions, test flows, linting checks, and secure release triggers."),
    CLOUD_DEPLOYMENT("Cloud Deployment Agent", Department.DEVOPS, "Deploys assets to live edge runners, configures SSL, and maps domains."),
    MONITORING("Monitoring Agent", Department.DEVOPS, "Provisions telemetry hooks, error trackers, and resource alerting logs."),
    AUTO_ROLLBACK("Auto Rollback Agent", Department.DEVOPS, "Listens to production error rates, triggering state rollbacks on high failure volumes."),

    // Documentation
    README("README Agent", Department.DOCUMENTATION, "Drafts user manuals, installation guides, and interactive quickstarts."),
    REPORT("Report Agent", Department.DOCUMENTATION, "Compiles full system diagnostic specifications and development summaries."),
    PPT("PPT Agent", Department.DOCUMENTATION, "Produces pitch deck outlines and technical slides representing project features."),
    API_DOCS("API Documentation Agent", Department.DOCUMENTATION, "Autogenerates OpenAPI/Swagger templates and API schema specs."),

    // User
    NOTIFICATION("Notification Agent", Department.USER, "Dispatches operational alerts, email digests, and build-state progress notes."),
    CHAT_ASSISTANT("Chat Assistant Agent", Department.USER, "Hosts conversational debug help and answers technical architectural questions.")
}

data class AppFile(
    val path: String,
    val content: String,
    val language: String,
    val size: String
)

data class LogEntry(
    val timestamp: Long,
    val agentName: String,
    val deptDisplayName: String,
    val msg: String,
    val severity: String // "INFO", "WARN", "ERROR", "SUCCESS"
)
