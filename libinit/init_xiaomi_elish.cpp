/*
 * Copyright (C) 2023 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <libinit_dalvik_heap.h>
#include "vendor_init.h"
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>

void property_override(std::string prop, std::string value) {
    auto pi = (prop_info *) __system_property_find(prop.c_str());
    if (pi != nullptr) {
        __system_property_update(pi, value.c_str(), value.length());
    } else {
        __system_property_add(prop.c_str(), prop.length(), value.c_str(), value.length());
    }
}

void vendor_load_properties() {
    set_dalvik_heap();

#ifdef __ANDROID_RECOVERY__
    property_override("ro.debuggable", "1");
    property_override("ro.adb.secure.recovery", "0");
#endif
}
