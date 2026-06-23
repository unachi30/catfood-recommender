$loginBody = '{"username":"admin","password":"admin123"}'
$login = Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
$token = $login.data.token
Write-Host "Token obtained"

$batchBody = @{
  category = "主食罐"
  brand = "ApiTestBrand"
  name = "API測試罐"
  flavors = @("口味A", "口味B")
  capacity = "80g"
  price = 100
  shopName = "Shopee"
  shopUrl = "https://example.com/test"
  inStock = $true
} | ConvertTo-Json -Depth 3

try {
  $headers = @{ Authorization = "Bearer $token" }
  $result = Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/admin/cans/batch" -Method Post -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes($batchBody)) -Headers $headers
  $result | ConvertTo-Json -Depth 5
} catch {
  Write-Host "Status:" $_.Exception.Response.StatusCode.value__
  $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
  Write-Host $reader.ReadToEnd()
}
