#!/bin/bash
for ARG in $*
do
	javac -cp .:$PATH:$VERTX_HOME/lib/* $ARG
done
