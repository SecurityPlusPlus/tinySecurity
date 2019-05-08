cmake_minimum_required(VERSION 3.4.1)

set(JNI_DIR ${CMAKE_SOURCE_DIR}/src/maim/cpp/)

aux_source_directory(${CMAKE_SOURCE_DIR}/src/maim/cpp/include SRC_H)
#指定了头文件所在的目录
include_directories(${SRC_H})

add_library(tinySecurity
        SHARED
        src/main/cpp/tinySecurity.cpp
        src/main/cpp/AesEncryptor.cpp
        src/main/cpp/aes.cpp
        src/main/cpp/base64.c
        src/main/cpp/md5.c)

find_library(
        log-lib
        log)

target_link_libraries(
        tinySecurity
        ${log-lib})
