#!/bin/sh

echo "$@" > /tmp/asm.s \
    && as -msyntax=intel -mmnemonic=intel -mnaked-reg  /tmp/asm.s -o /tmp/asm.o \
    && objdump -D -d -mi386:x86-64 -M intel /tmp/asm.o
