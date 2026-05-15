param(
  [switch]$NoRun
)

Write-Host "Building backend JAR and Docker image..." -ForegroundColor Cyan

Push-Location ..\
try {
  Write-Host "Running Maven package (skip tests)" -ForegroundColor Yellow
  if (Test-Path .\mvnw.cmd) {
    & .\mvnw.cmd -B -f backend\pom.xml clean package -DskipTests
  } else {
    mvn -B -f backend\pom.xml clean package -DskipTests
  }

  Write-Host "Docker build (from repo root)" -ForegroundColor Yellow
  docker build -f backend/Dockerfile -t tienda-backend:local-debug .

  if (-not $NoRun) {
    Write-Host "Running container on port 8081" -ForegroundColor Green
    docker run --rm -p 8081:8081 tienda-backend:local-debug
  } else {
    Write-Host "Built image tienda-backend:local-debug (not running)." -ForegroundColor Green
  }
} finally {
  Pop-Location
}
