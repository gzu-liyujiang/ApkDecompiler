#!/usr/bin/env bash
#
# author:李玉江<1032694760@qq.com>
#
# 切换到当前脚本所在的目录
echo "之前工作目录：${PWD}"
_CURRENT_FILE="${BASH_SOURCE[0]}"
_DIR_NAME="$(dirname "${_CURRENT_FILE}")"
echo "当前脚本路径：${_CURRENT_FILE}"
echo "脚本目录路径：${_DIR_NAME}"
cd "${_DIR_NAME}" || exit
echo "当前工作目录：${PWD}"

_DEB="out/ApkDecompiler.deb"
_DIST="dist/"
_OUT_JAR="out/artifacts/ApkDecompiler_jar/ApkDecompiler.jar"

function extractDeb() {
  echo "解开deb包到${_DIST}"
  # dpkg --help
  # 将deb包中的文件解压到指定的目录下
  dpkg --vextract ${_DEB} ${_DIST}
  # 将deb包中的控制信息解压到指定的目录下
  dpkg --control ${_DEB} ${_DIST}DEBIAN/
  echo "deb包${_DEB}解开到${_DIST}结束"
}

function builddDeb() {
  _APK_DEC="${_DIST}opt/ApkDecompiler/ApkDec.jar"
  rm ${_APK_DEC}
  cp ${_OUT_JAR} ${_APK_DEC}
  chmod +x ${_APK_DEC}
  chmod -R 0755 ${_DIST}DEBIAN
  # dpkg-deb --help
  # 将指定的目录下的所有文件打包为deb格式的文件
  dpkg-deb --build ${_DIST} ${_DEB}
  echo "从${_DIST}构建deb包为${_DEB}结束"
}

if [[ $1 == "--extract" || $1 == "-e" ]]; then
  extractDeb
elif [[ $1 == "--build" || $1 == "-b" ]]; then
  builddDeb
else
  echo ""
  echo "======================================="
  echo "使用说明：./deb.sh <option>"
  echo "打包：./deb.sh -b 或 ./deb.sh --build"
  echo "解包：./deb.sh -e 或 ./deb.sh --extract"
  echo "======================================="
fi
