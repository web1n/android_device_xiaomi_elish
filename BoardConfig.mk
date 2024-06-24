#
# Copyright (C) 2023 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from sm8250-common
include device/xiaomi/sm8250-common/BoardConfigCommon.mk

DEVICE_PATH := device/xiaomi/elish

# Board
TARGET_BOARD_INFO_FILE := $(DEVICE_PATH)/board-info.txt

BUILD_BROKEN_DUP_RULES := true

# Display
TARGET_SCREEN_DENSITY := 275

# HIDL
DEVICE_MANIFEST_FILE += $(DEVICE_PATH)/manifest.xml
DEVICE_FRAMEWORK_COMPATIBILITY_MATRIX_FILE += \
    $(DEVICE_PATH)/framework_compatibility_matrix_xiaomi.xml

# Init
TARGET_INIT_VENDOR_LIB := //$(DEVICE_PATH):init_xiaomi_elish
TARGET_RECOVERY_DEVICE_MODULES := init_xiaomi_elish

# Kernel
TARGET_KERNEL_CONFIG += vendor/xiaomi/elish.config

# OTA assert
TARGET_OTA_ASSERT_DEVICE := elish

# Properties
TARGET_VENDOR_PROP += $(DEVICE_PATH)/vendor.prop

# Sepolicy
SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += $(DEVICE_PATH)/sepolicy/private
SYSTEM_EXT_PUBLIC_SEPOLICY_DIRS += $(DEVICE_PATH)/sepolicy/public
BOARD_SEPOLICY_DIRS += $(DEVICE_PATH)/sepolicy/vendor

# Inherit from the proprietary version
include vendor/xiaomi/elish/BoardConfigVendor.mk
