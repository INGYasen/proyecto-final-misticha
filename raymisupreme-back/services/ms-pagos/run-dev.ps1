$env:STRIPE_SECRET_KEY = [Environment]::GetEnvironmentVariable('STRIPE_SECRET_KEY', 'User')
$env:MP_ACCESS_TOKEN = [Environment]::GetEnvironmentVariable('MP_ACCESS_TOKEN', 'User')
$env:MP_NOTIFICATION_URL = [Environment]::GetEnvironmentVariable('MP_NOTIFICATION_URL', 'User')

if ([string]::IsNullOrWhiteSpace($env:STRIPE_SECRET_KEY)) {
    throw 'STRIPE_SECRET_KEY no está configurada como variable de usuario en Windows.'
}

if ([string]::IsNullOrWhiteSpace($env:MP_ACCESS_TOKEN)) {
    throw 'MP_ACCESS_TOKEN no está configurada como variable de usuario en Windows.'
}

if ([string]::IsNullOrWhiteSpace($env:MP_NOTIFICATION_URL)) {
    throw 'MP_NOTIFICATION_URL no está configurada como variable de usuario en Windows.'
}

mvn spring-boot:run
