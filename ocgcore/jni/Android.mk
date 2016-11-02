LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := ocgcore
LOCAL_CFLAGS := -D_LUA_COMPAT_5_2 -fno-rtti -fno-exceptions -fstrict-aliasing

ifndef NDEBUG
LOCAL_CFLAGS += -g -D_DEBUG
else
LOCAL_CFLAGS += -fexpensive-optimizations -O3 
endif

ifeq ($(TARGET_ARCH_ABI),x86)
LOCAL_CFLAGS += -fno-stack-protector
endif

ifeq ($(TARGET_ARCH_ABI),armeabi-v8a)
LOCAL_CFLAGS += -D_ARM_X64
endif

ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
LOCAL_CFLAGS += -mno-unaligned-access
endif
LOCAL_C_INCLUDES := $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/lua

LOCAL_SRC_FILES := ocgcore.cpp \
                   main.cpp \
				   lua/lapi.c \
				   lua/lauxlib.c \
				   lua/lbaselib.c \
				   lua/lbitlib.c \
				   lua/lcode.c \
				   lua/lcorolib.c \
				   lua/lctype.c \
				   lua/ldblib.c \
				   lua/ldebug.c \
				   lua/ldo.c \
				   lua/ldump.c \
				   lua/lfunc.c \
				   lua/lgc.c \
				   lua/linit.c \
				   lua/liolib.c \
				   lua/llex.c \
				   lua/lmathlib.c \
				   lua/lmem.c \
				   lua/loadlib.c \
				   lua/lobject.c \
				   lua/lopcodes.c \
				   lua/loslib.c \
				   lua/lparser.c \
				   lua/lstate.c \
				   lua/lstring.c \
				   lua/lstrlib.c \
				   lua/ltable.c \
				   lua/ltablib.c \
				   lua/ltm.c \
				   lua/lundump.c \
				   lua/lvm.c \
				   lua/lzio.c \
				   ocgcore/card.cpp \
				   ocgcore/duel.cpp \
				   ocgcore/effect.cpp \
				   ocgcore/field.cpp \
				   ocgcore/group.cpp \
				   ocgcore/interpreter.cpp \
				   ocgcore/libcard.cpp \
				   ocgcore/libdebug.cpp \
				   ocgcore/libduel.cpp \
				   ocgcore/libeffect.cpp \
				   ocgcore/libgroup.cpp \
				   ocgcore/mem.cpp \
				   ocgcore/ocgapi.cpp \
				   ocgcore/operations.cpp \
				   ocgcore/playerop.cpp \
				   ocgcore/processor.cpp \
				   ocgcore/scriptlib.cpp \

LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)

