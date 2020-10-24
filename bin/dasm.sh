#!/bin/sh

bin=""
for byte in "$@"; do
    bin="$bin\\x$byte"
done

echo -n -e "$bin" > /tmp/asmed && objdump -D -b binary -mi386:x86-64 -M intel /tmp/asmed
