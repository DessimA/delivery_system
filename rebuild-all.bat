@echo off
echo ========================================
echo Reconstruindo TODOS os containers
echo ========================================
echo.

echo [1/4] Parando containers em execução...
docker-compose down

echo.
echo [2/4] Removendo todas as imagens antigas...
docker-compose rm -f

echo.
echo [3/4] Reconstruindo todos os serviços (sem cache)...
docker-compose build --no-cache

echo.
echo [4/4] Iniciando todos os serviços...
docker-compose up -d

echo.
echo ========================================
echo Pronto! Aguarde alguns segundos e acesse:
echo Frontend: http://localhost
echo Backend:  http://localhost:8080
echo ========================================
echo.
echo Para ver os logs, execute:
echo docker-compose logs -f
echo.
pause
