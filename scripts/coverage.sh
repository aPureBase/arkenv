#!/usr/bin/env sh

path="${1:-build/reports/jacocoRootReport/jacocoRootReport.csv}"

awk -F "," '
    { instructions += $4 + $5; covered += $5 }
    END {
        print 100 * covered / instructions, "% covered"
     }
   ' "${path}"
