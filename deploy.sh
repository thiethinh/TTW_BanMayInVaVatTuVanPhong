#!/bin/bash

echo "Docker Compose Rebuild"
echo "=========================="
docker compose down -v
docker compose up --build -d
echo
echo "Xong!"