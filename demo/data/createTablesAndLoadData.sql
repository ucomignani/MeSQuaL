CREATE TABLE ChicagoDataset 
(
timestamp VARCHAR(255),	
node_id VARCHAR(255),
subsystem VARCHAR(255),
sensor VARCHAR(255),
parameter VARCHAR(255),
value_raw VARCHAR(255),
value_hrf VARCHAR(255)
);

LOAD DATA INFILE '/var/lib/mysql-files/AoT_Chicago.complete.recent.csv' 
INTO TABLE ChicagoDataset
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;


CREATE TABLE Admissions
(
row_id VARCHAR(255),
subject_id VARCHAR(255),
hadm_id VARCHAR(255),
admittime VARCHAR(255),
dischtime VARCHAR(255),
deathtime VARCHAR(255),
admission_type VARCHAR(255),
admission_location VARCHAR(255),
discharge_location VARCHAR(255),
insurance VARCHAR(255),
language VARCHAR(255),
religion VARCHAR(255),
marital_status VARCHAR(255),
ethnicity VARCHAR(255),
edregtime VARCHAR(255),
edouttime VARCHAR(255),
diagnosis VARCHAR(255),
hospital_expire_flag VARCHAR(255),
has_chartevents_data VARCHAR(255)
);

LOAD DATA INFILE '/var/lib/mysql-files/ADMISSIONS.csv' 
INTO TABLE Admissions
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;

CREATE TABLE Patients
(
row_id VARCHAR(255),
subject_id VARCHAR(255),
gender VARCHAR(255),
dob VARCHAR(255),
dod VARCHAR(255),
dod_hosp VARCHAR(255),
dod_ssn VARCHAR(255),
expire_flag VARCHAR(255)
);

LOAD DATA INFILE '/var/lib/mysql-files/PATIENTS.csv' 
INTO TABLE Patients
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;
