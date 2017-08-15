#!/bin/bash

set -e

CURRENT=$1
NEXT=$2
VERSION_FILES="pom.xml README.md src/main/java/io/pdfdata/API.java"

function bump-versions {
    for f in $VERSION_FILES; do
        sed -i s/$1/$2/ $f
    done

    git add $VERSION_FILES
    git commit -m "$NEXT"
}

bump-versions $CURRENT $NEXT

bin/deploy.sh

bump-versions $NEXT $NEXT-SNAPSHOT
