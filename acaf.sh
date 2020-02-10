./gradlew clean build install

export BINTRAY_USER=ibm-iloom
export BINTRAY_KEY=20b56d9f26acaddc7c635b8c5f7e71af48cd9f44
./gradlew :clean assembleRelease :fragmentation:bintrayUpload