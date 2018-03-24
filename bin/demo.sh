#!/bin/bash


time java -Xmx8g -jar HIME_release.jar demo.txt 4 300  > tmp.log
grep -i Motif tmp.log | cut -d' ' -f 2- > res.txt
