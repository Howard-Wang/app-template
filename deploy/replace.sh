#!/bin/bash

# 服务端替换部署程序，然后重启
# 该脚本就是简单的替换下素材，方便测试而不是执行完整的 install 动作

srcFolder=$(pwd)
destFolder=""

error() {
  echo -e "\033[31m[error] $1\033[0m"
}

warning() {
  echo -e "\033[33m$1\033[0m"
}

info() {
  echo -e "\033[32m$1\033[0m"
}

usage() {
  echo "Usage: $0 [deploy folder]"
  echo ""
  echo "exp:"
  echo "  $0 ../app"
  echo "  $0 /data/company/apps/app"
}

restart() {
  # 此处必须使用绝对名称，否则会误杀其他进程，导致 replace.sh 执行失败
  tempName=`ls $destFolder/*.war`
  warName=`basename $tempName`
  echo "Kill ${warName}"

  ppids=$(ps -ef|grep $warName|grep -v grep|awk '{print $2}')
  for ppid in $ppids
  do
    kill -9 ${ppid}
  done

  cd $destFolder
  ./start.sh
  cd -
}

check() {
  ret=0

  if [ ! -d $srcFolder ]; then
    error "$srcFolder is not a folder"
    ret=1
  fi

  if [ ! -d $destFolder ]; then
    error "$destFolder is not a folder"
    ret=1
  fi

  startScriptPath=$destFolder/start.sh
  if [ ! -x $startScriptPath ]; then
    error "$startScriptPath not found or can not excute"
    ret=1
  fi

  appConfigPath=$destFolder/application-pro.yml
  if [ ! -f $appConfigPath ]; then
    error "$appConfigPath not found"
    ret=1
  fi

  name=`ls $srcFolder/*.war`
  if [ "$name" == "" ]; then
    error "$srcFolder not found any war"
    ret=1
  fi

  if [ $ret -ne 0 ]; then
    exit 1
  fi
}

killPID() {
  # 此处必须使用绝对名称，否则会误杀其他进程，导致 replace.sh 执行失败
  tempName=`ls $destFolder/*.war`
  warName=`basename $tempName`
  echo "Kill ${warName}"

  ppids=$(ps -ef|grep $warName|grep -v grep|awk '{print $2}')
  for ppid in $ppids
  do
    kill -9 ${ppid}
  done

}

replace() {
  rm $destFolder/*.war
  cp $srcFolder/*.war $destFolder/
  if [ $? -ne 0 ]; then
    error "cp $srcFolder/*.war to $destFolder"
    exit 1
  fi

  info "Note: start.sh and application.yml not replace"
  echo ""
}

if [ $# -ne 1 ]; then
  usage
  exit 1
fi

uid=$(id -u)
if [ $uid -ne 0 ]; then
    info "Please using root privelege to run this script."
    exit 1
fi

srcFolder="$srcFolder/app"
destFolder=$1

check
killPID
replace
restart
