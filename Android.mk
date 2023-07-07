#
# Copyright (C) 2023 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

LOCAL_PATH := $(call my-dir)

ifeq ($(TARGET_DEVICE),elish)
include $(call all-makefiles-under,$(LOCAL_PATH))
endif

# WiFi
$(shell mkdir -p $(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/qca6390; \
    ln -sf /vendor/etc/wifi/WCNSS_qcom_cfg.ini \
	    $(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/qca6390/WCNSS_qcom_cfg.ini; \
    ln -sf /mnt/vendor/persist/wlan/wlan_mac.bin \
	    $(TARGET_OUT_VENDOR)/firmware/wlan/qca_cld/qca6390/wlan_mac.bin)
