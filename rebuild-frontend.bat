@echo off
echo ========================================
echo Reconstruindo Frontend com as melhorias
echo ========================================
echo.

echo [1/4] Parando containers em execução...
docker-compose down

echo.
echo [2/4] Removendo imagem antiga do frontend...
docker rmi delivery_system-frontend-app -f

echo.
echo [3/4] Reconstruindo apenas o frontend (sem cache)...
docker-compose build --no-cache frontend-app

echo.
echo [4/4] Iniciando todos os serviços...
docker-compose up -d

echo.
echo ========================================
echo Pronto! Aguarde alguns segundos e acesse:
echo http://localhost
echo ========================================
echo.
echo Para ver os logs do frontend, execute:
echo docker-compose logs -f frontend-app
echo.
pause
