# Script d'installation et correction Angular
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  INSTALLATION TAILWIND CSS + CORRECTIONS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier qu'on est dans le bon dossier
$currentPath = Get-Location
$expectedPath = "d:\copiservice\services_repo\angular_project\my-login-app"

if ($currentPath.Path -ne $expectedPath) {
    Write-Host "⚠️  Changement de répertoire vers : $expectedPath" -ForegroundColor Yellow
    Set-Location $expectedPath
}

Write-Host "✅ Dossier courant : $(Get-Location)" -ForegroundColor Green
Write-Host ""

# Étape 1 : Vérifier Node et npm
Write-Host "🔍 Vérification de Node.js et npm..." -ForegroundColor Cyan
try {
    $nodeVersion = node --version
    $npmVersion = npm --version
    Write-Host "✅ Node.js version : $nodeVersion" -ForegroundColor Green
    Write-Host "✅ npm version : $npmVersion" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "❌ Node.js ou npm non trouvé ! Installez Node.js d'abord." -ForegroundColor Red
    exit 1
}

# Étape 2 : Installer Tailwind CSS
Write-Host "📦 Installation de Tailwind CSS, PostCSS et Autoprefixer..." -ForegroundColor Cyan
try {
    npm install -D tailwindcss postcss autoprefixer
    Write-Host "✅ Tailwind CSS installé avec succès !" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "❌ Erreur lors de l'installation de Tailwind CSS" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

# Étape 3 : Vérifier que tailwind.config.js existe
Write-Host "🔍 Vérification de tailwind.config.js..." -ForegroundColor Cyan
if (Test-Path "tailwind.config.js") {
    Write-Host "✅ tailwind.config.js trouvé !" -ForegroundColor Green
} else {
    Write-Host "❌ tailwind.config.js manquant ! Il devrait avoir été créé." -ForegroundColor Red
    Write-Host "💡 Créez-le manuellement si nécessaire." -ForegroundColor Yellow
}
Write-Host ""

# Étape 4 : Nettoyer le cache Angular
Write-Host "🧹 Nettoyage du cache Angular..." -ForegroundColor Cyan
if (Test-Path ".angular") {
    Remove-Item -Recurse -Force .angular
    Write-Host "✅ Cache .angular supprimé" -ForegroundColor Green
} else {
    Write-Host "ℹ️  Pas de cache .angular à supprimer" -ForegroundColor Gray
}
Write-Host ""

# Étape 5 : Afficher les fichiers corrigés
Write-Host "📝 Fichiers modifiés :" -ForegroundColor Cyan
Write-Host "  ✅ src/app/login/login.html (structure HTML nettoyée)" -ForegroundColor Green
Write-Host "  ✅ src/styles.css (styles globaux ajoutés)" -ForegroundColor Green
Write-Host "  ✅ tailwind.config.js (configuration Tailwind)" -ForegroundColor Green
Write-Host ""

# Étape 6 : Proposer de démarrer le serveur
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  INSTALLATION TERMINÉE !" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "🚀 Prêt à démarrer le serveur Angular !" -ForegroundColor Green
Write-Host ""
Write-Host "Commandes disponibles :" -ForegroundColor Yellow
Write-Host "  1. ng serve              (démarrer le serveur)" -ForegroundColor White
Write-Host "  2. npm start             (alternative)" -ForegroundColor White
Write-Host "  3. ng serve --open       (démarrer + ouvrir navigateur)" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Voulez-vous démarrer le serveur maintenant ? (O/N)"

if ($choice -eq "O" -or $choice -eq "o") {
    Write-Host ""
    Write-Host "🚀 Démarrage du serveur Angular..." -ForegroundColor Cyan
    Write-Host "📍 URL : http://localhost:4200" -ForegroundColor Green
    Write-Host "⏹️  Pour arrêter : Ctrl+C" -ForegroundColor Yellow
    Write-Host ""
    
    # Démarrer le serveur
    npm start
} else {
    Write-Host ""
    Write-Host "✅ Installation terminée. Démarrez manuellement avec :" -ForegroundColor Green
    Write-Host "   ng serve" -ForegroundColor White
    Write-Host ""
}
