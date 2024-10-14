#include <android-base/logging.h>

#include <vendor/xiaomi_elish/peripherals/1.0/IPeripherals.h>

namespace vendor {
namespace xiaomi_elish {
namespace peripherals {
namespace V1_0 {
namespace implementation {

using ::android::sp;
using ::android::hardware::Return;
using ::android::hardware::Void;

#define KEYBOARD_STATUS_PATH "/sys/devices/platform/soc/soc:xiaomi_keyboard/xiaomi_keyboard_conn_status"

#define TOUCH_DEV_PATH "/dev/xiaomi-touch"
#define SET_CUR_VALUE 0
#define TOUCH_STYLUS_MODE 20
#define TOUCH_MAGIC 0x5400
#define TOUCH_IOC_SETMODE (TOUCH_MAGIC + SET_CUR_VALUE)

#define STYLUS_DRIVER_VERSION 2

class Peripherals : public IPeripherals {
public:
    Peripherals() : mStylusEnabled(false), mKeyboardEnabled(false) {}

    Return<bool> isStylusEnabled() override;
    Return<bool> setStylusEnable(bool enable) override;
    Return<bool> isKeyboardEnabled() override;
    Return<bool> setKeyboardEnable(bool enable) override;
    Return<bool> isKeyboardConnected() override;

private:
    bool mStylusEnabled;
    bool mKeyboardEnabled;
};

}  // namespace implementation
}  // namespace V1_0
}  // namespace peripherals
}  // namespace xiaomi_elish
}  // namespace vendor
