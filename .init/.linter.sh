#!/bin/bash
cd /home/kavia/workspace/code-generation/tv-to-do-list-manager-91575/android_tv_todo_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

