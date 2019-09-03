#!/usr/bin/env sh

coreDir=arkenv
yamlDir=arkenv-yaml
path="${1:-build/reports/test/jacocoTestReport.csv}"

getStats() {
  awk -F "," '
    { instructions += $4 + $5; covered += $5 }
    END {
        print covered, instructions
     }
   ' "$1/$2"
}

res="$(getStats "$coreDir" "$path")"
covered=$(echo "$res" | awk '{print $1}')
instructions=$(echo "$res" | awk '{print $2}')

res="$(getStats "$yamlDir" "$path")"
covered2=$(echo "$res" | awk '{print $1}')
instructions2=$(echo "$res" | awk '{print $2}')

cov=$((covered + covered2))
ins=$((instructions + instructions2))

result=$((100 * cov / ins))
echo $result % covered
