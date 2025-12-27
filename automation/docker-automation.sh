#!/bin/bash

# Cores para output (Fix para compatibilidade)
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}===========================================${NC}"
echo -e "${BLUE}   DELIVERY SYSTEM - DOCKER AUTOMATION     ${NC}"
echo -e "${BLUE}===========================================${NC}"

# 1. Seleção de Target
echo -e "\n${YELLOW}Step 1: Select Target${NC}"
echo "1) Backend"
echo "2) Frontend"
echo "3) Both (Full System)"
read -p "Choice [1-3]: " target

# 2. Seleção de Ação
echo -e "\n${YELLOW}Step 2: Select Action${NC}"
echo "1) Clean (Down & Remove Volumes)"
echo "2) Rebuild (Force Build & Start)"
echo "3) Start (Up -d)"
read -p "Choice [1-3]: " action

execute_docker() {
    local service_display=$1
    local service_cmd=$2
    local act=$3
    
    # Navegar para a pasta infra onde o docker-compose.yml reside
    cd "$(dirname "$0")/../infra" || exit
    
    case $act in
        1) 
            echo -e "${BLUE}Cleaning ${service_display}...${NC}"
            docker-compose down -v $service_cmd 
            ;;
        2) 
            echo -e "${BLUE}Rebuilding ${service_display}...${NC}"
            docker-compose up -d --build $service_cmd 
            ;;
        3) 
            echo -e "${BLUE}Starting ${service_display}...${NC}"
            docker-compose up -d $service_cmd 
            ;;
    esac
}

case $target in
    1) execute_docker "Backend" "backend" $action ;; 
    2) execute_docker "Frontend" "frontend" $action ;; 
    3) execute_docker "Full System" "" $action ;; 
    *) echo "Invalid choice" ;; 
esac