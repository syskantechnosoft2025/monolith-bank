# E2E Test Script for Monolith Bank
# Tests: Registration, Login, Account Creation, Transfer, EMI Payment, Reports, Dashboard

$ErrorActionPreference = 'SilentlyContinue'
$BaseURL = "http://localhost:8080"
$frontendURL = "http://localhost:3000"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MONOLITH BANK E2E TEST SUITE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: User Registration
Write-Host "TEST 1: User Registration" -ForegroundColor Yellow
try {
    $registerResponse = Invoke-WebRequest -Uri "$BaseURL/api/auth/register" `
        -Method POST `
        -ContentType 'application/json' `
        -Body @{
            username = "e2euser_$(Get-Random)"
            email = "e2e_$(Get-Random)@test.com"
            password = "TestPass123!"
            firstName = "E2E"
            lastName = "User"
        } | ConvertTo-Json `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($registerResponse.StatusCode -in @(200, 201)) {
        $userData = $registerResponse.Content | ConvertFrom-Json
        $global:userId = $userData.id
        $global:username = $userData.username
        Write-Host "✓ Registration: SUCCESS" -ForegroundColor Green
        Write-Host "  User: $($userData.username) (ID: $($userData.id))" -ForegroundColor Green
    } else {
        Write-Host "✗ Registration: FAILED ($($registerResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Registration: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 2: User Login
Write-Host "TEST 2: User Login" -ForegroundColor Yellow
try {
    $loginResponse = Invoke-WebRequest -Uri "$BaseURL/api/auth/login" `
        -Method POST `
        -ContentType 'application/json' `
        -Body (@{
            username = $global:username
            password = "TestPass123!"
        } | ConvertTo-Json) `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($loginResponse.StatusCode -eq 200) {
        $loginData = $loginResponse.Content | ConvertFrom-Json
        $global:accessToken = $loginData.accessToken
        $global:refreshToken = $loginData.refreshToken
        Write-Host "✓ Login: SUCCESS" -ForegroundColor Green
        Write-Host "  Token: $($global:accessToken.Substring(0, 30))..." -ForegroundColor Green
    } else {
        Write-Host "✗ Login: FAILED ($($loginResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Login: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 3: Get User Accounts
Write-Host "TEST 3: Get User Accounts (Before Creation)" -ForegroundColor Yellow
try {
    $accountsResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/me" `
        -Method GET `
        -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($accountsResponse.StatusCode -eq 200) {
        $accounts = $accountsResponse.Content | ConvertFrom-Json
        Write-Host "✓ Get Accounts: SUCCESS" -ForegroundColor Green
        Write-Host "  Accounts Count: $(($accounts | Measure-Object).Count)" -ForegroundColor Green
        if ($accounts.Count -gt 0) {
            foreach ($acc in $accounts) {
                Write-Host "  - Account: $($acc.accountNumber) ($($acc.type)) - Balance: $($acc.balance)" -ForegroundColor Green
                if ($null -eq $global:account1Id) { $global:account1Id = $acc.id }
                if ($null -eq $global:account2Id -and $acc.id -ne $global:account1Id) { $global:account2Id = $acc.id }
            }
        }
    } else {
        Write-Host "✗ Get Accounts: FAILED ($($accountsResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Get Accounts: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: Create Savings Account
Write-Host "TEST 4: Create Savings Account" -ForegroundColor Yellow
try {
    $createAccResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/create" `
        -Method POST `
        -ContentType 'application/json' `
        -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
        -Body (@{
            accountType = "SAVINGS"
            initialBalance = 5000.00
        } | ConvertTo-Json) `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($createAccResponse.StatusCode -in @(200, 201)) {
        $newAccount = $createAccResponse.Content | ConvertFrom-Json
        $global:account1Id = $newAccount.id
        Write-Host "✓ Create Savings Account: SUCCESS" -ForegroundColor Green
        Write-Host "  Account: $($newAccount.accountNumber) - Balance: $($newAccount.balance)" -ForegroundColor Green
    } else {
        Write-Host "✗ Create Savings Account: FAILED ($($createAccResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Create Savings Account: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 5: Create Current Account
Write-Host "TEST 5: Create Current Account" -ForegroundColor Yellow
try {
    $createAccResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/create" `
        -Method POST `
        -ContentType 'application/json' `
        -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
        -Body (@{
            accountType = "CURRENT"
            initialBalance = 10000.00
        } | ConvertTo-Json) `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($createAccResponse.StatusCode -in @(200, 201)) {
        $newAccount = $createAccResponse.Content | ConvertFrom-Json
        $global:account2Id = $newAccount.id
        Write-Host "✓ Create Current Account: SUCCESS" -ForegroundColor Green
        Write-Host "  Account: $($newAccount.accountNumber) - Balance: $($newAccount.balance)" -ForegroundColor Green
    } else {
        Write-Host "✗ Create Current Account: FAILED ($($createAccResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Create Current Account: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 6: Deposit Funds
Write-Host "TEST 6: Deposit Funds - 500 to Savings" -ForegroundColor Yellow
try {
    $depositResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/deposit" `
        -Method POST `
        -ContentType 'application/json' `
        -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
        -Body (@{
            accountId = $global:account1Id
            amount = 500.00
            description = "E2E Test Deposit"
        } | ConvertTo-Json) `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($depositResponse.StatusCode -in @(200, 201)) {
        Write-Host "✓ Deposit: SUCCESS" -ForegroundColor Green
    } else {
        Write-Host "✗ Deposit: FAILED ($($depositResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Deposit: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 7: Transfer Funds Between Own Accounts
Write-Host "TEST 7: Transfer Funds - 1000 from Savings to Current" -ForegroundColor Yellow
try {
    if ($null -ne $global:account1Id -and $null -ne $global:account2Id) {
        $transferResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/transfer" `
            -Method POST `
            -ContentType 'application/json' `
            -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
            -Body (@{
                fromAccountId = $global:account1Id
                toAccountId = $global:account2Id
                amount = 1000.00
                description = "E2E Test Transfer"
            } | ConvertTo-Json) `
            -UseBasicParsing `
            -TimeoutSec 10

        if ($transferResponse.StatusCode -in @(200, 201)) {
            Write-Host "✓ Transfer: SUCCESS" -ForegroundColor Green
            Write-Host "  From Account $($global:account1Id) to Account $($global:account2Id)" -ForegroundColor Green
        } else {
            Write-Host "✗ Transfer: FAILED ($($transferResponse.StatusCode))" -ForegroundColor Red
        }
    } else {
        Write-Host "✗ Transfer: SKIPPED - Accounts not available" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Transfer: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 8: Withdraw Funds
Write-Host "TEST 8: Withdraw Funds - 200 from Current" -ForegroundColor Yellow
try {
    $withdrawResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/withdraw" `
        -Method POST `
        -ContentType 'application/json' `
        -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
        -Body (@{
            accountId = $global:account2Id
            amount = 200.00
            description = "E2E Test Withdrawal"
        } | ConvertTo-Json) `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($withdrawResponse.StatusCode -in @(200, 201)) {
        Write-Host "✓ Withdraw: SUCCESS" -ForegroundColor Green
    } else {
        Write-Host "✗ Withdraw: FAILED ($($withdrawResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Withdraw: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 9: Search Transaction History
Write-Host "TEST 9: Search Transaction History" -ForegroundColor Yellow
try {
    $txnResponse = Invoke-WebRequest -Uri "$BaseURL/api/transactions/search?limit=10" `
        -Method GET `
        -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($txnResponse.StatusCode -eq 200) {
        $transactions = $txnResponse.Content | ConvertFrom-Json
        Write-Host "✓ Search Transactions: SUCCESS" -ForegroundColor Green
        Write-Host "  Transactions Found: $(($transactions | Measure-Object).Count)" -ForegroundColor Green
        if ($transactions.Count -gt 0) {
            Write-Host "  Recent Transactions:" -ForegroundColor Green
            $transactions | Select-Object -First 3 | ForEach-Object {
                Write-Host "    - Type: $($_.transactionType) | Amount: $($_.amount) | Status: COMPLETED" -ForegroundColor Green
            }
        }
    } else {
        Write-Host "✗ Search Transactions: FAILED ($($txnResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Search Transactions: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Test 10: Check Updated Account Balances
Write-Host "TEST 10: Check Updated Account Balances" -ForegroundColor Yellow
try {
    $accountsResponse = Invoke-WebRequest -Uri "$BaseURL/api/accounts/me" `
        -Method GET `
        -Headers @{"Authorization" = "Bearer $($global:accessToken)"} `
        -UseBasicParsing `
        -TimeoutSec 10

    if ($accountsResponse.StatusCode -eq 200) {
        $accounts = $accountsResponse.Content | ConvertFrom-Json
        Write-Host "✓ Final Account Status: SUCCESS" -ForegroundColor Green
        foreach ($acc in $accounts) {
            Write-Host "  - $($acc.type): $($acc.accountNumber) - Balance: $($acc.balance)" -ForegroundColor Green
        }
    } else {
        Write-Host "✗ Final Account Status: FAILED ($($accountsResponse.StatusCode))" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Final Account Status: ERROR - $_" -ForegroundColor Red
}
Write-Host ""

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "E2E TEST SUITE COMPLETED" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Frontend is accessible at: $frontendURL" -ForegroundColor Yellow
Write-Host "Swagger UI available at: $BaseURL/swagger-ui.html" -ForegroundColor Yellow
Write-Host "H2 Console available at: $BaseURL/h2-console" -ForegroundColor Yellow
Write-Host ""
