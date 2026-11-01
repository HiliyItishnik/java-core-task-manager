param([int]$Port = 8080)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath (Join-Path $PSScriptRoot 'task-manager')
try {
    if (-not (Get-Command javac -ErrorAction SilentlyContinue)) { throw 'Install JDK 17 or newer and add it to PATH.' }
    New-Item -ItemType Directory -Force -Path 'target/classes' | Out-Null
    $sources = @(Get-ChildItem -LiteralPath 'src/main/java' -Filter '*.java' -Recurse | ForEach-Object FullName)
    & javac --release 17 -encoding UTF-8 -d target/classes $sources
    if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
    Write-Host "Open http://127.0.0.1:$Port in your browser. Keep this window open."
    Start-Process "http://127.0.0.1:$Port"
    & java -cp target/classes ru.portfolio.taskmanager.WebMain $Port
    if ($LASTEXITCODE -ne 0) { throw 'Server failed. If the port is busy, run: .\start.ps1 -Port 8081' }
} catch { Write-Error $_; exit 1 }
