@echo off
echo ========================================
echo Iniciando Ambiente de PRODUCAO
echo ========================================
echo.

echo [1/3] Parando containers de desenvolvimento...
docker-compose -f docker-compose.dev.yml down

echo.
echo [2/3] Reconstruindo para producao...
docker-compose -f docker-compose.yml build --no-cache

echo.
echo [3/3] Iniciando em modo PRODUCAO...
docker-compose -f docker-compose.yml up -d

echo.
echo ========================================
echo PRONTO! Ambiente de producao iniciado
echo ========================================
echo.
echo Servicos disponiveis:
echo   Frontend:  http://localhost
echo   Backend:   http://localhost:8080
echo   Database:  localhost:5432
echo.
echo Para ver os logs:
echo   docker-compose logs -f
echo.
echo Para PARAR:
echo   docker-compose down
echo.
pause
