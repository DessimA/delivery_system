#!/bin/bash

# Cores para output
GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${CYAN}===========================================${NC}"
echo -e "${CYAN}   DELIVERY SYSTEM - LOCAL AUTOMATION      ${NC}"
echo -e "${CYAN}===========================================${NC}"

# 1. Seleção de Target
echo -e "\n${YELLOW}Step 1: Select Target${NC}"
echo "1) Backend (Java/Spring)"
echo "2) Frontend (Vue/Vite)"
echo "3) Both"
read -p "Choice [1-3]: " target

# 2. Seleção de Ação
echo -e "\n${YELLOW}Step 2: Select Action${NC}"
echo "1) Clean (Remove artifacts)"
echo "2) Rebuild (Install & Compile)"
echo "3) Start (Run Application)"
read -p "Choice [1-3]: " action

# Salvar diretório raiz da automação
AUTO_DIR="$(cd "$(dirname "$0")" && pwd)"

execute_backend() {
    echo -e "\n${CYAN}--- Backend Operations ---${NC}"
    cd "$AUTO_DIR/../backend" || exit
    case $1 in
        1) echo -e "${GREEN}Cleaning Backend...${NC}"; mvn clean ;;
        2) echo -e "${GREEN}Rebuilding Backend...${NC}"; mvn clean install -DskipTests ;;
        3) echo -e "${GREEN}Starting Backend...${NC}"; mvn spring-boot:run ;;
    esac
}

execute_frontend() {
    echo -e "\n${CYAN}--- Frontend Operations ---${NC}"
    cd "$AUTO_DIR/../frontend" || exit
    case $1 in
        1) echo -e "${GREEN}Cleaning Frontend...${NC}"; rm -rf dist node_modules/.vite target ;;
        2) echo -e "${GREEN}Rebuilding Frontend...${NC}"; npm install ;;
        3) echo -e "${GREEN}Starting Frontend...${NC}"; npm run dev ;;
    esac
}

case $target in
    1) execute_backend $action ;;
    2) execute_frontend $action ;;
    3) 
        execute_backend $action
        execute_frontend $action 
        ;;
    *) echo "Invalid choice" ;;
esac
