#!/bin/bash
set -e

echo "Building seat-booking backend..."
mvn clean package -DskipTests

echo "Build completed successfully!"
