#!/bin/bash

current_file="${BASH_SOURCE[0]}"
dir_name="$(dirname "$current_file")"
echo "$current_file"
echo "$dir_name"
cd "$dir_name"

#打包
dpkg-deb --build dist/ ApkDecompiler.deb

