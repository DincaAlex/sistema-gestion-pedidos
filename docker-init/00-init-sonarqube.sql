-- Crear usuario específico para SonarQube
CREATE USER sonarqube WITH ENCRYPTED PASSWORD 'sonarqube';

-- Crear base de datos para SonarQube con el owner correcto
CREATE DATABASE sonarqube OWNER sonarqube;

-- Conectarse a la base de datos
\c sonarqube postgres;

-- Otorgar privilegios en el schema public
GRANT ALL ON SCHEMA public TO sonarqube;
ALTER SCHEMA public OWNER TO sonarqube;
