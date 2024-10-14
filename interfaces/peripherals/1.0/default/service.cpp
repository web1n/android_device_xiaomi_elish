#include "Peripherals.h"

#include <hidl/HidlTransportSupport.h>

using android::hardware::configureRpcThreadpool;
using android::hardware::joinRpcThreadpool;
using android::sp;

using vendor::xiaomi_elish::peripherals::V1_0::IPeripherals;
using vendor::xiaomi_elish::peripherals::V1_0::implementation::Peripherals;

int main() {
    sp<IPeripherals> service = new Peripherals();

    configureRpcThreadpool(1, true /*callerWillJoin*/);

    if (service->registerAsService() != android::OK) {
        LOG(ERROR) << "Can't register Peripherals HAL service";
        return 1;
    }

    joinRpcThreadpool();

    return 0; // should never get here
}
