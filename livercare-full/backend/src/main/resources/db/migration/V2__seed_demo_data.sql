-- V2__seed_demo_data.sql
-- Demo patient + lab results for hackathon presentation
-- Meera Pillai — HCC Stage III on Sorafenib showing drift pattern

INSERT INTO patients (
  patient_code, full_name, date_of_birth, gender,
  diagnosis, primary_treatment, enrollment_date,
  current_risk_level, drift_score, baseline_established,
  baseline_start_date, baseline_end_date
) VALUES (
  'PT-2024-0001', 'Meera Pillai', '1963-04-15', 'FEMALE',
  'HCC Stage III', 'Sorafenib 400mg BD', '2024-01-10',
  'HIGH', 74, TRUE, '2024-01-10', '2024-03-10'
);

-- ── Baseline labs (days 1–45) ──────────────────────────────
INSERT INTO lab_results (patient_id, lab_date, alt, ast, bilirubin, inr, albumin, is_baseline, treatment_cycle)
VALUES
  (1, '2024-01-10', 32.0, 28.0, 0.80, 1.00, 3.80, TRUE,  'Sorafenib Cycle 1'),
  (1, '2024-01-24', 34.0, 30.0, 0.82, 1.02, 3.75, TRUE,  'Sorafenib Cycle 1'),
  (1, '2024-02-07', 31.0, 27.0, 0.78, 0.99, 3.82, TRUE,  'Sorafenib Cycle 2'),
  (1, '2024-02-21', 35.0, 31.0, 0.85, 1.03, 3.78, TRUE,  'Sorafenib Cycle 2'),
  (1, '2024-03-06', 33.0, 29.0, 0.81, 1.01, 3.80, TRUE,  'Sorafenib Cycle 3'),
  (1, '2024-03-10', 34.0, 30.0, 0.83, 1.02, 3.77, TRUE,  'Sorafenib Cycle 3'),
-- ── Post-baseline monitoring (drift starts appearing) ─────
  (1, '2024-03-20', 38.0, 34.0, 1.10, 1.12, 3.65, FALSE, 'Sorafenib Cycle 4'),
  (1, '2024-03-27', 44.0, 38.0, 1.40, 1.22, 3.55, FALSE, 'Sorafenib Cycle 4'),
  (1, '2024-04-03', 52.0, 43.0, 1.75, 1.35, 3.40, FALSE, 'Sorafenib Cycle 4'),
  (1, '2024-04-08', 61.0, 49.0, 2.20, 1.48, 3.25, FALSE, 'Sorafenib Cycle 4'),
  (1, '2024-04-13', 67.0, 54.0, 2.80, 1.60, 3.10, FALSE, 'Sorafenib Cycle 4 Day 23');

-- Second demo patient — low risk, stable
INSERT INTO patients (
  patient_code, full_name, date_of_birth, gender,
  diagnosis, primary_treatment, enrollment_date,
  current_risk_level, drift_score, baseline_established,
  baseline_start_date, baseline_end_date
) VALUES (
  'PT-2024-0002', 'Ramesh Kumar', '1958-09-22', 'MALE',
  'HCC Stage II', 'Lenvatinib 12mg OD', '2024-02-01',
  'LOW', 12, TRUE, '2024-02-01', '2024-04-01'
);

INSERT INTO lab_results (patient_id, lab_date, alt, ast, bilirubin, inr, albumin, is_baseline, treatment_cycle)
VALUES
  (2, '2024-02-01', 28.0, 24.0, 0.70, 0.98, 4.00, TRUE,  'Lenvatinib Cycle 1'),
  (2, '2024-02-15', 30.0, 26.0, 0.72, 0.99, 3.95, TRUE,  'Lenvatinib Cycle 1'),
  (2, '2024-03-01', 27.0, 23.0, 0.68, 0.97, 4.02, TRUE,  'Lenvatinib Cycle 2'),
  (2, '2024-03-15', 29.0, 25.0, 0.71, 1.00, 3.98, TRUE,  'Lenvatinib Cycle 2'),
  (2, '2024-04-01', 31.0, 27.0, 0.73, 1.01, 3.96, FALSE, 'Lenvatinib Cycle 3'),
  (2, '2024-04-13', 30.0, 26.0, 0.74, 1.00, 3.97, FALSE, 'Lenvatinib Cycle 3');
