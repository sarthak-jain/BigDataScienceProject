#!/bin/bash
rm listings2
for i in 1 2 3; do
   curl "http://streeteasy.com/for-rent/nyc?page=${i}&view=list" > page$i
   sed -n -e '/<tr id="listing/,/<\/tr>/ p' page$i > page${i}_listings
   sed 's/\($\)\([0-9|,]*\)\(.*\)/\2/g' page${i}_listings >> listings2
   rm page${i}_listings
   rm page$i
done

