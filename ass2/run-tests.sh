#!/bin/sh
filename=$(date)

for i in  {1..5}
do
   result=$(java Nqueen 2> /dev/null)
   echo $result
   echo $result >> tests/$filename.txt
done
