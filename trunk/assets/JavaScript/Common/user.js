function UserInfo(){
	this.merchId = "";
	this.machineId = "";
	this.userName = "";
    this.userStatus = "";
	this.gradeId = "";
    this.token = null;
    this.reloginAction = function(){};
    this.merchIdSetResultAction = function(){};
    this.machineIdSetResultAction = function(){};
}

UserInfo.prototype.init =  function(data){
	this.userName = data.userName;
    this.userStatus = data.userStatus;
	this.gradeId = data.gradeId;
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

UserInfo.prototype.setSignInResult = function(func){
    this.afterSignInAction = function(data){
        if(func){
            func(data);
        }
    };
};


window.user = new UserInfo();
window.merchSettings = null;
window.payTemplates = null;
window.voidTemplates = null;
window.refundTemplates = null;
