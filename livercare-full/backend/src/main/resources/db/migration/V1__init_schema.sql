-- V1__init_schema.sql

CREATE TABLE patients (
    id                   BIGSERIAL PRIMARY KEY,
    patient_code         VARCHAR(20)  NOT NULL UNIQUE,
    full_name            VARCHAR(100) NOT NULL,
    date_of_birth        DATE         NOT NULL,
    gender               VARCHAR(10)  NOT NULL CHECK (gender IN ('MALE','FEMALE','OTHER')),
    diagnosis            VARCHAR(100),
    primary_treatment    VARCHAR(100),
    enrollment_date      DATE         NOT NULL,
    current_risk_level   VARCHAR(20)  NOT NULL DEFAULT 'LOW' CHECK (current_risk_level IN ('LOW','MODERATE','HIGH')),
    drift_score          INTEGER      NOT NULL DEFAULT 0,
    baseline_established BOOLEAN      NOT NULL DEFAULT FALSE,
    baseline_start_date  DATE,
    baseline_end_date    DATE,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE lab_results (
    id               BIGSERIAL PRIMARY KEY,
    patient_id       BIGINT           NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    lab_date         DATE             NOT NULL,
    alt              DOUBLE PRECISION NOT NULL CHECK (alt > 0),
    ast              DOUBLE PRECISION NOT NULL CHECK (ast > 0),
    bilirubin        DOUBLE PRECISION NOT NULL CHECK (bilirubin > 0),
    inr              DOUBLE PRECISION NOT NULL CHECK (inr > 0),
    albumin          DOUBLE PRECISION,
    alp              DOUBLE PRECISION,
    notes            VARCHAR(300),
    is_baseline      BOOLEAN          NOT NULL DEFAULT FALSE,
    treatment_cycle  VARCHAR(50),
    created_at       TIMESTAMPTZ      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lab_patient_date ON lab_results(patient_id, lab_date DESC);
CREATE INDEX idx_lab_baseline     ON lab_results(patient_id, is_baseline);

CREATE TABLE drift_alerts (
    id                    BIGSERIAL PRIMARY KEY,
    patient_id            BIGINT      NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    lab_result_id         BIGINT      REFERENCES lab_results(id),
    drift_score           INTEGER     NOT NULL,
    risk_level            VARCHAR(20) NOT NULL CHECK (risk_level IN ('LOW','MODERATE','HIGH')),
    marker_weights_json   TEXT,
    flagged_markers       TEXT,
    ai_explanation        TEXT,        -- from AI drift engine (statistical)
    bedrock_narrative     TEXT,        -- from Claude 3 Sonnet via Bedrock
    days_ahead_threshold  INTEGER,
    resolved              BOOLEAN     NOT NULL DEFAULT FALSE,
    resolved_at           TIMESTAMPTZ,
    resolved_by           VARCHAR(100),
    resolution_notes      TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_alert_patient    ON drift_alerts(patient_id);
CREATE INDEX idx_alert_unresolved ON drift_alerts(patient_id, resolved);
CREATE INDEX idx_alert_risk       ON drift_alerts(risk_level, resolved);

-- Audit log
CREATE TABLE audit_log (
    id          BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50),
    entity_id   BIGINT,
    action      VARCHAR(50),
    actor       VARCHAR(100),
    details     TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
