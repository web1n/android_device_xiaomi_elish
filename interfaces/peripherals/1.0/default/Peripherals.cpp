#include "Peripherals.h"
#include <fstream>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>

namespace vendor {
namespace xiaomi_elish {
namespace peripherals {
namespace V1_0 {
namespace implementation {

Return<bool> Peripherals::isStylusEnabled() {
    LOG(INFO) << "isStylusEnabled " << mStylusEnabled;

    return mStylusEnabled;
}

Return<bool> Peripherals::setStylusEnable(bool enable) {
    if (mStylusEnabled == enable) {
        LOG(INFO) << "Stylus already in desired state.";
        return true;
    }

    int fd = open(TOUCH_DEV_PATH, O_RDWR);
    if (fd < 0) {
        LOG(ERROR) << "Failed to open " << TOUCH_DEV_PATH;
        return false;
    }

    int flag = (enable ? 0x10 : 0x00) | STYLUS_DRIVER_VERSION;
    int arg[2] = {TOUCH_STYLUS_MODE, flag};
    if (ioctl(fd, TOUCH_IOC_SETMODE, &arg) < 0) {
        LOG(ERROR) << "Failed to set stylus mode.";
        close(fd);
        return false;
    }
    close(fd);

    LOG(INFO) << "Stylus changed " << enable;
    mStylusEnabled = enable;
    return true;
}

Return<bool> Peripherals::isKeyboardEnabled() {
    LOG(INFO) << "isKeyboardEnabled " << mKeyboardEnabled;

    return mKeyboardEnabled;
}

Return<bool> Peripherals::setKeyboardEnable(bool enable) {
    if (mKeyboardEnabled == enable) {
        LOG(INFO) << "Keyboard already in desired state.";
        return true;
    }

    std::ofstream file(KEYBOARD_STATUS_PATH);
    if (!file.is_open()) {
        LOG(ERROR) << "Failed to open " << KEYBOARD_STATUS_PATH;
        return false;
    }
    file << (enable ? "enable_keyboard" : "disable_keyboard");
    if (file.fail()) {
        LOG(ERROR) << "Failed to write " << KEYBOARD_STATUS_PATH;
        file.close();
        return false;
    }
    file.close();

    LOG(INFO) << "Keyboard changed " << enable;
    mKeyboardEnabled = enable;
    return true;
}

Return<bool> Peripherals::isKeyboardConnected() {
    std::ifstream file(KEYBOARD_STATUS_PATH);
    if (!file.is_open()) {
        LOG(ERROR) << "Failed to open " << KEYBOARD_STATUS_PATH;
        return false;
    }

    std::string content;
    if (!std::getline(file, content)) {
        LOG(ERROR) << "Failed to read " << KEYBOARD_STATUS_PATH;
        file.close();
        return false;
    }
    file.close();

    LOG(INFO) << "keyboard state " << content;
    return content == "1";
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace peripherals
}  // namespace xiaomi_elish
}  // namespace vendor
