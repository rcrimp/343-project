#!/bin/sh
filename=$(date)
COUNT=100
SUM=0

i=0
while [ $i -lt $COUNT ]; do
   result=$(java Nqueen java main board 8 pop 50 gen 100 res 10 keep 0.2 mut 0.05 2> /dev/null)
   echo $result >> tests/$filename.txt
   echo $result
   #SUM=$((SUM+$result))
   let i=i+1
done

sort -h "tests/$filename.txt"

#echo "average is: "
#echo "$SUM / $COUNT" | bc -l
