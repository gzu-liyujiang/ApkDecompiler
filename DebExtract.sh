#!/bin/bash

current_file="${BASH_SOURCE[0]}"
dir_name="$(dirname "$current_file")"
echo "$current_file"
echo "$dir_name"
cd "$dir_name"

#see http://blog.csdn.net/yygydjkthh/article/details/36695243
#解压出包中的文件
dpkg --vextract ./ApkDecompiler.deb dist/
#解压出包的控制信息：
dpkg --control ./ApkDecompiler.deb dist/DEBIAN/

