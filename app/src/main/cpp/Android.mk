LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := OpenSLESDemo
LOCAL_SRC_FILES := OpenSLESDemo.cpp record.c play.c
LOCAL_LDLIBS := -llog
LOCAL_LDLIBS    += -lOpenSLES

include $(BUILD_SHARED_LIBRARY)
