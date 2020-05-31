#!/bin/bash

##############################
#
# 该脚本主要实现应用的自动部署
#
##############################


error() {
    echo -e "\033[31m $(date "+%Y-%m-%d %H:%M:%S") [error] $@ \033[0m" | tee -a ${install_log}
}

warning() {
    echo -e "\033[33m $(date "+%Y-%m-%d %H:%M:%S") [warning] $@ \033[0m" | tee -a ${install_log}
}

info() {
    echo -e "\033[32m $(date "+%Y-%m-%d %H:%M:%S") [info] $@ \033[0m" | tee -a ${install_log}
}

# interrupt install
interrupt() {
    error "$@"
    show_useage
    exit 1
}

# useage
show_useage() {
    message="please read README.md, maybe help you install app !"
    echo -e "\033[34m $(date "+%Y-%m-%d %H:%M:%S") [info] ${message} \033[0m" | tee -a ${install_log}
}

db_check() {
    [ ! -z "${db_host}" ] || interrupt "no configration: db host item"
    [ ! -z "${db_port}" ] || interrupt "no configration: db port item"
    [ ! -z "${db_dbname}" ] || interrupt "no configration: db dbname item"
    [ ! -z "${db_username}" ] || interrupt "no configration: db username item"
    [ ! -z "${db_password}" ] || interrupt "no configration: db password item"
}

# 初始安装信息变量
init() {
    info "init ..."

    # 获取当前路径
    curr_path=$(dirname $(readlink -f $0))

    # 配置文件
    conf_file=${curr_path}/app_conf.sh
    if [[ ! -f "${conf_file}" ]]; then
        interrupt "$conf_file not found"
    fi

    # 日志输出文件
    install_log=${curr_path}/install.log

    # 初始化数据库脚本
    init_sql=init.sql
    if [[ ! -f "${init_sql}" ]]; then
        interrupt "$init_sql not found"
    fi

    # 解压后的目录名称
    unzip_file=app
    if [[ ! -d "${unzip_file}" ]]; then
        interrupt "$unzip_file not found"
    fi
}

# 环境检查
# 1. 检查相关不常用的命令是否存在
# 2. 检查依赖的组件/软件
check_env() {
    info "check env ..."

    ret=0

    if ! hash java 2>/dev/null; then
        error "java not found"
        ret=1
    fi

    if ! hash jar 2>/dev/null; then
        error "jar not found"
        ret=1
    fi

    if ! hash mysql 2>/dev/null; then
        error "mysql not found"
        ret=1
    fi

    if [ $ret -ne 0 ]; then
        exit 1
    fi
}

# 加载配置文件
load_conf() {
    info "load conf ..."

    [ -f ${conf_file} ] || interrupt "${conf_file} is not exist !"
    . ${conf_file} || interrupt "load ${conf_file} failed !"
}

# 修改备份的部署文件
modify_cfg_item() {
    info "modify cfg item ..."

    app_cfg="${curr_path}/${unzip_file}/application-pro.yml"

    # 校验数据库参数是否配置
    db_check

    jdbc_url_old="jdbc:mysql://127.0.0.1:3306/app_template"
    jdbc_url_new="jdbc:mysql://${db_host}:${db_port}/${db_dbname}"
    sed -i "s#${jdbc_url_old}#${jdbc_url_new}#g" ${app_cfg} || interrupt "modify jdbc url failed !"

    sed -i "s/username: .*/username: ${db_username}/g" ${app_cfg} || interrupt "modify jdbc username failed !"

    sed -i "s/password: .*/password: ${db_password}/g" ${app_cfg} || interrupt "modify jdbc password failed !"

    [ ! -z "${server_port}" ] || interrupt "no configration: server port item"
    sed -i "s#port: .*#port: ${server_port}#g" ${app_cfg} || interrupt "modify server port failed !"

    [ ! -z "${service_host}" ] || interrupt "no configration: service host item"
    sed -i "s#serverHost: .*#serverHost: ${service_host}#g" ${app_cfg} || interrupt "modify service host failed !"

    [ ! -z "${log_path}" ] || interrupt "no configration: log path item"
    sed -i "s#log-path: .*#log-path: ${log_path}#g" ${app_cfg} || interrupt "modify log path failed !"
}

copy_to_dest_path() {
    info "copy to dest path..."

    [ ! -z "${install_path}" ] || interrupt "no configration: install path item "
    info "copy ${curr_path}/${unzip_file} to ${install_path}"

    mkdir -p ${install_path}

    [ -d "${install_path}/${unzip_file}" ] && rm -rf "${install_path}/${unzip_file}"

    cp -rf ${unzip_file} ${install_path} ||  interrupt "copy failed"
}

init_database() {
    if [ "install" != "${install_style}" ];then
        return
    fi

    info "init database ..."

    # 校验数据库参数是否配置
    db_check

    [ -f ${curr_path}/${init_sql} ] || interrupt "${init_sql} is not exist !"

    # 初始化数据库连接信息
    mysql_connection_cmd="mysql -u${db_username} -p${db_password} -h${db_host} -P${db_port} -D${db_dbname}"
    ${mysql_connection_cmd} < ${curr_path}/${init_sql}
    if [ $? -ne 0 ]; then
        interrupt "init database failed!"
    fi
}

main() {
    info "install starting ..."

    # 初始安装信息变量
    init

    # 环境检查
    check_env

    # 加载配置文件
    load_conf

    # 修改解压包中的配置文件
    modify_cfg_item

    # 将修改配置的解压文件拷贝到安装目录
    copy_to_dest_path

    # 初始化数据库
    init_database

    echo ""
    echo "Install Ok! Please go to ${install_path}/${unzip_file} to run ./start.sh"
}

if [ $# -eq 0 ];then
    echo "Usage: ./install.sh install"
    echo ""
    echo "Please read README.md first"
    exit 1
fi

install_style=$1

main

