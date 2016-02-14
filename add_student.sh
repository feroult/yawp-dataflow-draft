#!/bin/bash

STUDENT_ID=$1
GRADE_ID=$2
BACKEND_API=${FLOWT_API:-http://localhost:8080/api}

curl -H "Content-type: application/json" -X POST -d "{ id: '/students/$STUDENT_ID', gradeId: '/grades/$GRADE_ID' }" $BACKEND_API/students; echo
