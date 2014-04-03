package cn.koolcloud.ipos.appstore.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableApp implements Parcelable {
	private String id;
	private String name;
	private String version;
	private String size;
	private String icon;
	private String downloadId;
	private String rating;
	private long date;
	private String vender;
	private int versionCode;
	private String packageName;

	public ParcelableApp(String id, String name, String version, String size,
			String icon, String downloadId, String rating, long date, String vender,
			int versionCode, String packageName) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.size = size;
		this.icon = icon;
		this.downloadId = downloadId;
		this.rating = rating;
		this.date = date;
		this.vender = vender;
		this.versionCode = versionCode;
		this.packageName = packageName;
	}
	
	public ParcelableApp(Parcel source) {
        super();
        this.setId(source.readString());
        this.setName(source.readString());
        this.setVersion(source.readString());
        this.setSize(source.readString());
        this.setIcon(source.readString());
        this.setDownloadId(source.readString());
        this.setRating(source.readString());
        this.setDate(source.readLong());
        this.setVender(source.readString());
        this.setVersionCode(source.readInt());
        this.setPackageName(source.readString());
    }

	public ParcelableApp() {
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getVender() {
		return vender;
	}

	public void setVender(String vender) {
		this.vender = vender;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}

	/**
	* @Title: getIconFileName
	* @Description: generate icon name
	* @param @return icon_id_.png eg:647_152_.png
	* @return String 
	* @throws
	*/
	public String getIconFileName() {
		return this.getIcon() + "_" + this.getId() + "_"  + ".png";
	}
	
	public String getSnapShortImageName(String snapImgId) {
		return snapImgId + "_" + this.name + "_" + this.id + "_" + ".png"; 
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeString(id);
		parcel.writeString(name);
		parcel.writeString(version);
		parcel.writeString(size);
		parcel.writeString(icon);
		parcel.writeString(downloadId);
		parcel.writeString(rating);
		parcel.writeLong(date);
		parcel.writeString(vender);
		parcel.writeInt(versionCode);
		parcel.writeString(packageName);
		
	}
	
	public void readFromParcel(Parcel in) {
	    id = in.readString();
	    name = in.readString();
	    version = in.readString();
	    size = in.readString();
	    icon = in.readString();
	    downloadId = in.readString();
	    rating = in.readString();
	    date = in.readLong();
	    vender = in.readString();
	    versionCode = in.readInt();
	    packageName = in.readString();
	}
	
	public static final Parcelable.Creator<ParcelableApp> CREATOR = new Parcelable.Creator<ParcelableApp>() {  
        public ParcelableApp createFromParcel(Parcel source) {  
            return new ParcelableApp(source);  
        }  
 
        public ParcelableApp[] newArray(int size) {  
            return new ParcelableApp[size];  
        }  
    };
}