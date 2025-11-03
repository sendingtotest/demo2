# PowerShell script to create sample users for the demo2 REST API
# Usage:
# 1. Start the app locally (or set $baseUrl to your deployed app URL)
# 2. Run this script in PowerShell: .\scripts\create-sample-users.ps1

$baseUrl = $env:BASE_URL
if (-not $baseUrl) {
  $baseUrl = 'http://localhost:8080'
}

$users = @(
  @{ name = 'Alice'; email = 'alice@example.com' },
  @{ name = 'Bob'; email = 'bob@example.com' },
  @{ name = 'Carla'; email = 'carla@example.com' },
  @{ name = 'Dan'; email = 'dan@example.com' },
  @{ name = 'Eve'; email = 'eve@example.com' }
)

foreach ($u in $users) {
  $json = $u | ConvertTo-Json
  try {
    $resp = Invoke-RestMethod -Uri "$baseUrl/users" -Method Post -Body $json -ContentType 'application/json'
    Write-Host "Created user:" $resp
  } catch {
    Write-Host "Failed to create user: $($u.name) — $_" -ForegroundColor Red
  }
}

Write-Host "Done. Check $baseUrl/users to view created users."
