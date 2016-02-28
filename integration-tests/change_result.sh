#!/bin/bash

RESULT_ID=$1
MAX=$2
MIN=$3
BACKEND_API=${FLOWT_API:-http://localhost:8080/api}

curl -H "Content-type: application/json" -X PATCH -d "{ id: '/results/$RESULT_ID', max: $MAX, min: $MIN }" $BACKEND_API/results/$RESULT_ID; echo
