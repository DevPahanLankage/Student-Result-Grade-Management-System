# Run from the project root (same folder as build.xml)
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$jar = Join-Path $projectRoot "dist\Student_Result___Grade_Management_System.jar"
if (-not (Test-Path $jar)) {
    Write-Host "JAR not found. Run Clean and Build in NetBeans first, or: ant clean jar"
    exit 1
}

if (-not (Test-Path "config\db.properties")) {
    Write-Host "Missing config\db.properties. Copy config\db.properties.example and set MySQL credentials."
    exit 1
}

java -jar $jar
