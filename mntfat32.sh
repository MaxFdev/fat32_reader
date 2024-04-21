#!/bin/bash

if [ -d "/mnt/fat32" ]; then
    echo "Directory /mnt/fat32 already exists."
else
    echo "Directory /mnt/fat32 created."
    mkdir /mnt/fat32
fi

mount -o loop fat32.img /mnt/fat32
echo "File system 'fat32' mounted at /mnt/fat32."