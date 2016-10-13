#!/bin/bash
rm listings
for i in {1..786}; do
   if [[ $((i % 10)) -eq 0 ]]; then
      echo $i;
   fi
   curl "http://streeteasy.com/for-rent/nyc?page=${i}&view=list" > page$i 2> /dev/null
   sed -n '/dataLayer/p' page$i > page${i}_dataLayer
   sed 's/\({\"id\"\)/\n\1/g' page${i}_dataLayer > page$i
   sed '1d' page$i > page${i}_2
   sed 's/\({.*}\),\(.*\)/\1/g' page${i}_2 > page$i
   head -n -2 page$i >> listings
   rm page${i}_2
   rm page${i}_dataLayer
   rm page$i
done
