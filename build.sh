#！/bin/bash
#
# build 和 打包脚本
#

deployFolder="app-deploy"
deployFile="${deployFolder}.tgz"

NPM_CMD="cnpm"

error() {
  echo -e "\033[31m[error] $1\033[0m"
}

warning() {
  echo -e "\033[33m$1\033[0m"
}

info() {
  echo -e "\033[32m$1\033[0m"
}

# 检查命令是否存在
checkCmd() {
  ret=0

  if ! hash git 2>/dev/null; then
    error "git not found"
    ret=1
  fi

  if ! hash java 2>/dev/null; then
    error "java not found"
    ret=1
  fi

  if ! hash $NPM_CMD 2>/dev/null; then
    error "$NPM_CMD not found"
    info "Please check https://npm.taobao.org/"
    ret=1
  fi

  if [ $ret -ne 0 ]; then
    exit 1
  fi
}

buildAll() {
  # 删除　build 目录
  ./gradlew clean

  cd web || { echo "cd web Failure"; exit 1; }

  echo "" && info ">>> build web app" && echo ""
  cd app || { echo "cd app"; exit 1; }
  npmBuild
  cd ..

  echo "" && info ">>> build web admin" && echo ""
  cd admin || { echo "cd admin Failure"; exit 1; }
  pwd
  npmBuild prod
  cd ..

  cd ..

  info ">>> move web build files to /build/www"
  echo ""

  mkdir -p "build/www/admin/"

  cp -r web/app/dist/* "build/www"
  cp -r web/admin/dist/* "build/www/admin/"

  info ">>> build backend"
  echo ""

  if [ "$mode" == "prod" ]; then
    echo "** BUILD PROD **"
    ./gradlew build -Pprod
  else
    echo "** BUILD DEV **"
    ./gradlew build
  fi

  if [ $? -ne 0 ];then
    error "build backend failed"
    exit 1
  fi

  jarName=$(basename "$(ls build/libs/app*)")
  if [ ! -f "build/libs/${jarName}" ]; then
    error "${jarName} not existed pwd: $(pwd)"
    exit 1
  fi
}

npmBuild() {
  $NPM_CMD install
  if [ $? -ne 0 ];then
    error "$NPM_CMDinstall failed"
    exit 1
  fi

  if [ $1 == 'prod' ];then
    $NPM_CMD run build:prod
  else
    $NPM_CMD run build
  fi

  if [ $? -ne 0 ];then
    error "$NPM_CMD run build failed"
    exit 1
  fi

  if [ ! -d dist ]; then
    error "dist folder not existed"
    exit 1
  fi
}

pack() {
  cp deploy/ $deployFolder -R
  cp build/libs/app*.war "$deployFolder/app"

  if [ $? -ne 0 ]; then
    error "pack error, please check .war, .sql existed"
    exit 1
  fi

  tar czf $deployFile $deployFolder
}

clean() {
  if [ -d $deployFolder ]; then
    rm -rf $deployFolder
  fi

  if [ -f $deployFile ]; then
    rm -rf $deployFile
  fi

  if [ -d build/libs ]; then
    rm -rf build/libs
  fi

  if [ -d web/build ]; then
    rm -rf web/build
  fi

  if [ -d web/app/dist ]; then
    rm -rf web/app/dist
  fi

  if [ -d web/admin/dist ]; then
    rm -rf web/admin/dist
  fi
}

usage() {
  echo "Usage: $0 [mode]"
  echo ""
  echo "mode:"
  echo "  dev  : development mode package, use h2 database"
  echo "  prod : production mode package, use mysql database"
}

if [ $# -ne 1 ]; then
  usage
  exit 1
fi

mode=$1

if [ "$mode" != "prod" ] && [ "$mode" != "dev" ]; then
  error "unknown mode $mode"
  exit 1
fi

checkCmd

clean
buildAll
pack

echo "Deploy successfully, Please check $deployFile"
