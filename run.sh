#!/bin/bash


time java -Xmx8g Run dishwasher.txt 4 300  > tmp.log
grep -i "Motif" tmp.log | cut -d' ' -f 2- > res.txt
