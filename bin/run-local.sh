#!/bin/sh

exec java \
-Duser.timezone=Europe/Moscow \
-Dfile.encoding=UTF-8 \
-jar ../target/gracefull-shutodwn-1.0-SNAPSHOT.jar