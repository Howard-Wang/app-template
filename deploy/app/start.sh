#!/bin/bash

war_name=$(basename app*.war)
nohup java -Xms512m -Xmx512m -XX:+UseG1GC -Dfile.encoding=UTF-8 -jar "$war_name" --spring.config.additional-location=application-pro.yml &
