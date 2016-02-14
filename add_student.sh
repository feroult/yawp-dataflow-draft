#!/bin/bash

STUDENT_ID=$1
GRADE_ID=$2

curl -H "Content-type: application/json" -X POST -d "{ id: '/students/$STUDENT_ID', gradeId: '/grades/$GRADE_ID' }" http://localhost:8080/api/students; echo
