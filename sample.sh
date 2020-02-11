#!/usr/bin/env bash
export BINTRAY_USER=ibm-iloom
export BINTRAY_API_KEY=20b56d9f26acaddc7c635b8c5f7e71af48cd9f44
export version=1.0.0.1
./gradlew build bintrayUpload