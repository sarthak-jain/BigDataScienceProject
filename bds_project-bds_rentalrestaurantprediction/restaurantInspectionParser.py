import csv
import collections
import json

input_list = []

with open("DOHMH_NYC_Restaurant_Inspection_Results_minColumns.csv") as csvfile:
  data = csv.DictReader(csvfile)
  for row in data:
    cleaned = { k:v.strip() for k, v in row.iteritems()}
    input_list.append(cleaned)

def get_unique_items(all_items, key="DBA"):
  seen_values = set()
  without_dupes = []
  for item in all_items:
    value = item[key]
    if value not in seen_values:
      without_dupes.append(item)
      seen_values.add(value)
  return without_dupes   


if __name__ == "__main__":
  uniq = get_unique_items(input_list)
  print("\n===== UNIQUE =====\n")
  for i in uniq:
    print(json.dumps(i, indent=2))