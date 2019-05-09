cmake_minimum_required(VERSION 3.4.1)

set(JNI_DIR ${CMAKE_SOURCE_DIR})

aux_source_directory(${JNI_DIR}/include SRC_H)
#指定了头文件所在的目录
include_directories(${SRC_H})

add_library(tinySecurity
        SHARED
        tinySecurity.cpp
        aesEncryptor.cpp
        aes.cpp
        base64.c
        md5.c)

find_library(
        log-lib
        log)

target_link_libraries(
        tinySecurity
        ${log-lib})
