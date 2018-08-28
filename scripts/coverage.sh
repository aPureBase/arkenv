#!/usr/bin/env bash

argument=$1
path="${argument:-build/reports/test/jacocoTestReport.csv}"

awk -F "," '
    { instructions += $4 + $5; covered += $5 }
    END {
        print covered, "/", instructions, "instructions covered";
        print 100 * covered / instructions, "% covered"
     }
   ' "${path}"
