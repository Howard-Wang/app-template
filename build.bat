@rem
@rem Copyright © 2020 the original author or authors.
@rem
@rem Permission is hereby granted, free of charge, to any person obtaining 
@rem a copy of this software and associated documentation files (the “Software”), 
@rem to deal in the Software without restriction, including without limitation 
@rem the rights to use, copy, modify, merge, publish, distribute, sublicense, 
@rem and/or sell copies of the Software, and to permit persons to whom the 
@rem Software is furnished to do so, subject to the following conditions:
@rem
@rem THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS 
@rem OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
@rem FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
@rem THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
@rem LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
@rem FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
@rem DEALINGS IN THE SOFTWARE.
@rem
@rem The above copyright notice and this permission notice shall be included 
@rem in all copies or substantial portions of the Software.
@rem

@rem
@rem 应用模板项目 Windows 编译脚本
@rem

@echo off

@rem 如果是国内用户建议使用 cnpm
set NPM_CMD=npm

set argC=0
for %%x in (%*) do Set /A argC+=1

if NOT %argC% == 1 (
  call:usage
  goto exit
)

set mode=%1%

if %mode%==dev (
  @rem FIXME 此处如果放到函数 checkEnv 中，则出错不能退出脚本
  set JAVA_EXE=java.exe
  call %JAVA_EXE% -version >NUL 2>&1
  if NOT "%ERRORLEVEL%" == "0" (
    call:echoRed "Can not found %JAVA_EXE%"
    goto exit
  )

  call %NPM_CMD% -version >NUL 2>&1
  if NOT "%ERRORLEVEL%" == "0" (
    call:echoRed "Can not found %NPM_CMD%"
    goto exit
  )

  call:clean
  call:build
  goto exit
)

if %mode%==prod (
  set JAVA_EXE=java.exe
  call %JAVA_EXE% -version >NUL 2>&1
  if NOT "%ERRORLEVEL%" == "0" (
    call:echoRed "Can not found %JAVA_EXE%"
    goto exit
  )

  call %NPM_CMD% -version >NUL 2>&1
  if NOT "%ERRORLEVEL%" == "0" (
    call:echoRed "Can not found %NPM_CMD%"
    goto exit
  )

  echo Note: Please check mysql configuration has been set properly in [92mapplication-pro.yml[0m
  timeout /T 10
  echo.

  call:clean
  call:build
  goto exit
)

@rem 没有命令被命中
call:usage
goto exit

:usage
  echo build.bat [mode]
  echo.
  echo mode:
  echo   prod : production mode package, use mysql database
  echo   dev  : development mode package, use h2 database
GOTO:EOF

@rem FIXME
@rem 在该函数中，如果有错误, 则:
@rem 1. 无法调用 goto exit, 2. 获取不到返回值, 所以暂未使用
:checkEnv
  call:echoGreen "Check Env..."

  set JAVA_EXE=java.exe
  call %JAVA_EXE% -version >NUL 2>&1
  if NOT "%ERRORLEVEL%" == "0" (
    call:echoRed "Can not found %JAVA_EXE%"
    GOTO:EOF
  )

  call %NPM_CMD% -version >NUL 2>&1
  if NOT "%ERRORLEVEL%" == "0" (
    call:echoRed "Can not found %NPM_CMD%"
    GOTO:EOF
  )

  echo.
GOTO:EOF

:build
  call:echoGreen "Build..."

  cd web

  cd admin
  call %NPM_CMD% install
  call %NPM_CMD% run build:prod
  if NOT EXIST dist (
    cd ../..
    call:echoRed "Build web admin failed"
    GOTO:EOF
  )
  cd ..

  cd app
  call %NPM_CMD% install
  call %NPM_CMD% run build
  if NOT EXIST dist (
    cd ../..
    call:echoRed "Build web app failed"
    GOTO:EOF
  )
  cd ../..

  mkdir build
  move web\app\dist build\www
  move web\admin\dist build\www\admin

  if "%mode%"=="prod" (
    call gradlew.bat build -Pprod
  ) else (
    call gradlew.bat build
  )

  echo.

  echo Please go to [92mbuild\libs[0m to find war file
GOTO:EOF

:clean
  call:echoGreen "Clean..."

  if EXIST web\admin\dist (
    echo Remove web\admin\dist
    rd /s/q web\admin\dist
  )

  if EXIST web\admin\node_modules (
    echo Remove web\admin\node_modules
    rd /s/q web\admin\node_modules
  )

  if EXIST web\app\node_modules (
    echo Remove web\app\node_modules
    rd /s/q web\app\node_modules
  )

  if EXIST web\app\dist (
    echo Remove web\app\dist
    rd /s/q web\app\dist
  )

  call gradlew.bat clean

  echo.
GOTO:EOF

:echoRed
  echo [91m%~1[0m
GOTO:EOF

:echoGreen
  echo [92m%~1[0m
GOTO:EOF

:echoYellow
  echo [93m%~1[0m
GOTO:EOF

:exit
