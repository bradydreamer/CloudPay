package cn.koolcloud.ipos.appstore.service.aidl;
import cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp;

interface IMSCService {  
    ParcelableApp checkUpdate(String packageName, int versionCode);
    void openAppDetail(inout ParcelableApp app);
}