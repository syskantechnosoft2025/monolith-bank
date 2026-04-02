# E2E Test Script for Monolith Bank
# Tests: Registration, Login, Account Creation, Transfer, Transactions, Reports, Dashboard

$ErrorActionPreference = 'SilentlyContinue'
$BaseURL = "http://localhost:8080"
$FrontendURL = "http://localhost:3000"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MONOLITH BANK E2E TEST SUITE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Pre-flight check: Verify backend is running
Write-Host "PRE-FLIGHT CHECK: Backend Status" -ForegroundColor Yellow
$backendRunning = $false
try {
    $statusResponse = Invoke-WebRequest -Uri "$BaseURL/h2-console" -Method GET -UseBasicParsing -TimeoutSec 5
    if ($statusResponse.StatusCode -eq 200) {
        Write-Host "  + SUCCESS: Backend is running on port 8080" -ForegroundColor Green
        $backendRunning = $true
    }
} catch {
    Write-Host "  - FAILED: Backend not responding. Please start the Spring Boot application first." -ForegroundColor Red
    Write-Host "    Run: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Prepare test data
$randomId = Get-Random -Maximum 9999
$testUsername = "e2euser_$randomId"
$testEmail = "e2e_$randomId@test.com"
$testPassword = "TestPass123!"

# Test 1: User Registration
Write-Host "TEST 1: User Registration" -ForegroundColor Yellow
Write-Host "  Note: Using in-memory H2 database - data persists only during this test run" -ForegroundColor Yellow
$regSuccess = $false
try {
    $regPayload = @{
        username = $testUsername
        email = $testEmail
        password = $testPassword
        firstName = "E2E"
        lastName = "User"
    } | ConvertTo-Json
    
    $regResponse = Invoke-WebRequest -Uri "$BaseURL/api/auth/register" -Method POST -ContentType 'application/json' -Body $regPayload -UseBasicParsing -TimeoutSec 10
    
    if ($regResponse.StatusCode -in @(200, 201)) {
        $userData = $regResponse.Content | ConvertFrom-Json
        $script:UserId = $userData.id
        $script:Username = $testUsername
        Write-Host "  + SUCCESS: User $($userData.username) created" -ForegroundColor Green
        $regSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 2: User Login
Write-Host "TEST 2: User Login" -ForegroundColor Yellow
$loginSuccess = $false
try {
    $loginPayload = @{
        username = $script:Username
        password = $testPassword
    } | ConvertTo-Json

    Write-Host "  Debug: Login payload: $loginPayload" -ForegroundColor Yellow
    Write-Host "  Debug: Attempting login with username: $($script:Username)" -ForegroundColor Yellow
    
    $loginResponse = Invoke-WebRequest -Uri "$BaseURL/api/auth/login" -Method POST -ContentType 'application/json' -Body $loginPayload -UseBasicParsing -TimeoutSec 10
    
    if ($loginResponse.StatusCode -eq 200) {
        $loginData = $loginResponse.Content | ConvertFrom-Json
        $script:AccessToken = $loginData.accessToken
        $script:RefreshToken = $loginData.refreshToken
        Write-Host "  + SUCCESS: Login successful, token obtained" -ForegroundColor Green
        $loginSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 3: Create Savings Account
Write-Host "TEST 3: Create Savings Account" -ForegroundColor Yellow
$savingsSuccess = $false
try {
    $savingsPayload = @{
        accountType = "SAVINGS"
        initialBalance = 5000.00
    } | ConvertTo-Json
    
    $headers = @{"Authorization" = "Bearer $($script:AccessToken)"}
    $savingsResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/create" -Method POST -ContentType 'application/json' -Headers $headers -Body $savingsPayload -UseBasicParsing -TimeoutSec 10
    
    if ($savingsResponse.StatusCode -in @(200, 201)) {
        $account = $savingsResponse.Content | ConvertFrom-Json
        $script:SavingsId = $account.id
        $script:SavingsNumber = $account.accountNumber
        Write-Host "  + SUCCESS: Savings Account $($account.accountNumber) created with balance INR $($account.balance)" -ForegroundColor Green
        $savingsSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: Create Current Account
Write-Host "TEST 4: Create Current Account" -ForegroundColor Yellow
$currentSuccess = $false
try {
    $currentPayload = @{
        accountType = "CURRENT"
        initialBalance = 10000.00
    } | ConvertTo-Json
    
    $headers = @{"Authorization" = "Bearer $($script:AccessToken)"}
    $currentResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/create" -Method POST -ContentType 'application/json' -Headers $headers -Body $currentPayload -UseBasicParsing -TimeoutSec 10
    
    if ($currentResponse.StatusCode -in @(200, 201)) {
        $account = $currentResponse.Content | ConvertFrom-Json
        $script:CurrentId = $account.id
        $script:CurrentNumber = $account.accountNumber
        Write-Host "  + SUCCESS: Current Account $($account.accountNumber) created with balance INR $($account.balance)" -ForegroundColor Green
        $currentSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 5: Deposit Funds
Write-Host "TEST 5: Deposit Funds to Savings" -ForegroundColor Yellow
$depositSuccess = $false
try {
    $depositPayload = @{
        accountId = $script:SavingsId
        amount = 500.00
        description = "E2E Test Deposit"
    } | ConvertTo-Json
    
    $headers = @{"Authorization" = "Bearer $($script:AccessToken)"}
    $depositResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/deposit" -Method POST -ContentType 'application/json' -Headers $headers -Body $depositPayload -UseBasicParsing -TimeoutSec 10
    
    if ($depositResponse.StatusCode -in @(200, 201)) {
        Write-Host "  + SUCCESS: Deposited INR 500 to Savings Account" -ForegroundColor Green
        $depositSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 6: Transfer Funds Between Accounts
Write-Host "TEST 6: Transfer Funds Between Accounts" -ForegroundColor Yellow
$transferSuccess = $false
try {
    if ($savingsSuccess -and $currentSuccess) {
        $transferPayload = @{
            fromAccountId = $script:SavingsId
            toAccountId = $script:CurrentId
            amount = 1000.00
            description = "E2E Test Transfer"
        } | ConvertTo-Json
        
        $headers = @{"Authorization" = "Bearer $($script:AccessToken)"}
        $transferResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/transfer" -Method POST -ContentType 'application/json' -Headers $headers -Body $transferPayload -UseBasicParsing -TimeoutSec 10
        
        if ($transferResponse.StatusCode -in @(200, 201)) {
            Write-Host "  + SUCCESS: Transferred INR 1000 from Savings to Current" -ForegroundColor Green
            $transferSuccess = $true
        }
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 7: Withdraw Funds
Write-Host "TEST 7: Withdraw Funds from Current" -ForegroundColor Yellow
$withdrawSuccess = $false
try {
    $withdrawPayload = @{
        accountId = $script:CurrentId
        amount = 200.00
        description = "E2E Test Withdrawal"
    } | ConvertTo-Json
    
    $headers = @{"Authorization" = "Bearer $($script:AccessToken)"}
    $withdrawResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/withdraw" -Method POST -ContentType 'application/json' -Headers $headers -Body $withdrawPayload -UseBasicParsing -TimeoutSec 10
    
    if ($withdrawResponse.StatusCode -in @(200, 201)) {
        Write-Host "  + SUCCESS: Withdrawn INR 200 from Current Account" -ForegroundColor Green
        $withdrawSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 8: Get Transaction History
Write-Host "TEST 8: Get Transaction History" -ForegroundColor Yellow
$txnSuccess = $false
try {
    $headers = @{"Authorization" = "Bearer $($script:AccessToken)"}
    $txnResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/search?limit=10" -Method GET -Headers $headers -UseBasicParsing -TimeoutSec 10
    
    if ($txnResponse.StatusCode -eq 200) {
        $transactions = $txnResponse.Content | ConvertFrom-Json
        $txnCount = @($transactions).Count
        Write-Host "  + SUCCESS: Retrieved $txnCount transactions" -ForegroundColor Green
        $txnSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 9: Get Account Balance Summary
Write-Host "TEST 9: Get Account Balances" -ForegroundColor Yellow
$balanceSuccess = $false
try {
    $headers = @{"Authorization" = "Bearer $($script:AccessToken)"}
    $balanceResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/me" -Method GET -Headers $headers -UseBasicParsing -TimeoutSec 10
    
    if ($balanceResponse.StatusCode -eq 200) {
        $accounts = $balanceResponse.Content | ConvertFrom-Json
        Write-Host "  + SUCCESS: Retrieved account balances:" -ForegroundColor Green
        foreach ($acc in @($accounts)) {
            Write-Host "    - $($acc.type): INR $($acc.balance)" -ForegroundColor Green
        }
        $balanceSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 10: Check Backend Status
Write-Host "TEST 10: Backend Status Check" -ForegroundColor Yellow
$statusSuccess = $false
try {
    $statusResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/me" -Method OPTIONS -UseBasicParsing -TimeoutSec 10
    if ($statusResponse.StatusCode -eq 200) {
        Write-Host "  + SUCCESS: Backend is responsive on port 8080" -ForegroundColor Green
        $statusSuccess = $true
    }
} catch {
    Write-Host "  - FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "E2E TEST SUITE SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Test Results:" -ForegroundColor Yellow
Write-Host "  1. Registration: $( if ($regSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($regSuccess) { 'Green' } else { 'Red' })
Write-Host "  2. Login: $( if ($loginSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($loginSuccess) { 'Green' } else { 'Red' })
Write-Host "  3. Create Savings: $( if ($savingsSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($savingsSuccess) { 'Green' } else { 'Red' })
Write-Host "  4. Create Current: $( if ($currentSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($currentSuccess) { 'Green' } else { 'Red' })
Write-Host "  5. Deposit: $( if ($depositSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($depositSuccess) { 'Green' } else { 'Red' })
Write-Host "  6. Transfer: $( if ($transferSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($transferSuccess) { 'Green' } else { 'Red' })
Write-Host "  7. Withdraw: $( if ($withdrawSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($withdrawSuccess) { 'Green' } else { 'Red' })
Write-Host "  8. Transactions: $( if ($txnSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($txnSuccess) { 'Green' } else { 'Red' })
Write-Host "  9. Balances: $( if ($balanceSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($balanceSuccess) { 'Green' } else { 'Red' })
Write-Host "  10. Backend Status: $( if ($statusSuccess) { 'PASS' } else { 'FAIL' } )" -ForegroundColor $(if ($statusSuccess) { 'Green' } else { 'Red' })
Write-Host ""
Write-Host "Service URLs:" -ForegroundColor Cyan
Write-Host "  Backend: $BaseURL" -ForegroundColor Yellow
Write-Host "  Frontend: $FrontendURL" -ForegroundColor Yellow
Write-Host "  Swagger UI: $BaseURL/swagger-ui.html" -ForegroundColor Yellow
Write-Host "  H2 Console: $BaseURL/h2-console" -ForegroundColor Yellow
Write-Host ""

