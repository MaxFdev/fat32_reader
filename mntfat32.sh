#!/bin/bash

if [ -d "/mnt/fat32" ]; then
    echo "Directory /mnt/fat32 already exists."
else
    mkdir /mnt/fat32 || exit 1
    echo "Directory /mnt/fat32 created."
fi

mount -o loop fat32.img /mnt/fat32 || exit 1
echo "File system 'fat32' mounted at /mnt/fat32."