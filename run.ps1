# Smart Blood Donation Network - Build & Launch Script

Write-Host "=============================================" -ForegroundColor Red
Write-Host "   Smart Blood Donation Network - Launcher   " -ForegroundColor Red
Write-Host "=============================================" -ForegroundColor Red
Write-Host "" 

# Clean or create bin folder 

if (-not (Test-Path -Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "Compiling Java files..." -ForegroundColor Gray

# Compile all the modules
javac -d bin src/App.java src/model/*.java src/service/*.java src/ui/*.java src/ui/components/*.java


if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation Successful! Running application..." -ForegroundColor Green
    Write-Host ""
    # Launch application with classpath pointing to bin
    java -cp bin App
} else {
    Write-Host "Compilation Failed. Please review errors above." -ForegroundColor Red
}
