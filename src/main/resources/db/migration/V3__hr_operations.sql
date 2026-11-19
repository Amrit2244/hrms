CREATE TABLE attendance_records (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    employee_id UUID NOT NULL REFERENCES employees(id),
    attendance_date DATE NOT NULL,
    check_in TIME,
    check_out TIME,
    status VARCHAR(20) NOT NULL DEFAULT 'PRESENT',
    notes VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_attendance_employee_date UNIQUE(employee_id, attendance_date),
    CONSTRAINT ck_attendance_status CHECK (status IN ('PRESENT','ABSENT','HALF_DAY','WORK_FROM_HOME','ON_LEAVE')),
    CONSTRAINT ck_attendance_times CHECK (check_out IS NULL OR check_in IS NULL OR check_out >= check_in)
);
CREATE INDEX idx_attendance_org_date ON attendance_records(organization_id, attendance_date DESC);
CREATE INDEX idx_attendance_employee_date ON attendance_records(employee_id, attendance_date DESC);

CREATE TABLE leave_requests (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    employee_id UUID NOT NULL REFERENCES employees(id),
    leave_type VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by VARCHAR(80),
    reviewed_at TIMESTAMPTZ,
    reviewer_comment VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_leave_dates CHECK (end_date >= start_date),
    CONSTRAINT ck_leave_status CHECK (status IN ('PENDING','APPROVED','REJECTED'))
);
CREATE INDEX idx_leave_org_status ON leave_requests(organization_id, status, created_at DESC);
CREATE INDEX idx_leave_employee_dates ON leave_requests(employee_id, start_date, end_date);

CREATE TABLE company_holidays (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    holiday_date DATE NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_holiday_org_date UNIQUE(organization_id, holiday_date)
);
CREATE INDEX idx_holidays_org_date ON company_holidays(organization_id, holiday_date);
