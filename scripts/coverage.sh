#!/usr/bin/env sh

path="${1:-build/reports/test/jacocoTestReport.csv}"

awk -F "," '
    { instructions += $4 + $5; covered += $5 }
    END {
        print covered, "/", instructions, "instructions covered";
        print 100 * covered / instructions, "% covered"
     }
   ' "${path}"
