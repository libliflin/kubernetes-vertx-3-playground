#!/bin/bash

# Log files
EVERYTHING_LOG="everything.log"

# Clean up old logs
> "$EVERYTHING_LOG"

# Stream pod statuses to the log file
kubectl get pods --all-namespaces --watch --chunk-size=0 | while read line; do
  echo "$(date '+[%Y-%m-%d %H:%M:%S]') $line"
done >> "$EVERYTHING_LOG" &

# Stream pod logs dynamically
kubectl get pods --all-namespaces --watch --no-headers | grep --line-buffered "1/1   Running" | while read -r namespace pod _; do
    kubectl logs -n "$namespace" "$pod" -f --prefix=true --timestamps=true >> "$EVERYTHING_LOG" 2>/dev/null &
done &

# Stream cluster events
kubectl get events --all-namespaces --watch >> "$EVERYTHING_LOG" &
