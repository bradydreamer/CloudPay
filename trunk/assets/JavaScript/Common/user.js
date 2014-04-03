function UserInfo(){
	this.merchId = "";
	this.machineId = "";
	this.userName = "";
    this.userStatus = "";
    this.token = null;
    this.reloginAction = function(){};
    this.merchIdSetResultAction = function(){};
    this.machineIdSetResultAction = function(){};
}

UserInfo.prototype.init =  function(data){
	this.userName = "";
    this.userStatus = data.userStatus;
    this.token = data.token;
    ServiceMerchInfo.setInfo(data);
};

UserInfo.prototype.setLoginResult = function(func){
    this.reloginAction = function(data){
        if(func){
            func(data);
        }
    };
};

UserInfo.prototype.setMerchIdResult = function(func){
    this.merchIdSetResultAction = function(data){
        if(func){
            func(data);
        }
    };
};

UserInfo.prototype.setMachineIdResult = function(func){
    this.machineIdSetResultAction = function(data){
        if(func){
            func(data);
        }
    };
};

window.user = new UserInfo();
window.merchSettings = null;
window.saleTemplates = null;
window.voidTemplates = null;
window.refundTemplates = null;
