@echo off
echo ========================================
echo Iniciando Ambiente de DESENVOLVIMENTO
echo com HOT-RELOAD ativado
echo ========================================
echo.

echo [1/2] Parando e removendo containers, redes e volumes antigos...
docker-compose -f docker-compose.dev.yml down -v --remove-orphans
docker-compose -f docker-compose.yml down -v --remove-orphans

echo.
echo [2/2] Construindo imagens e iniciando todos os servicos em modo DEV...
docker-compose -f docker-compose.dev.yml up -d --build --force-recreate

echo.
echo ========================================
echo PRONTO! Ambiente de desenvolvimento iniciado
echo ========================================
echo.
echo Servicos disponiveis:
echo   Frontend (DEV):  http://localhost:5173
echo   Backend (API):   http://localhost:8080
echo   Database:        localhost:5432
echo.
echo HOT-RELOAD ATIVO para o frontend.
echo Para o backend, reinicie o container para aplicar as mudancas.
echo.
echo Para ver os logs em tempo real:
echo   docker-compose -f docker-compose.dev.yml logs -f
echo.
echo Para PARAR o ambiente e remover os volumes:
echo   docker-compose -f docker-compose.dev.yml down -v
echo.
pause
