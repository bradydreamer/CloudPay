require.config({
	baseUrl: 'app',
    paths: {
    	/* Lib */
        Moo: '../libs/mootools-core-1.5.0',
        
        /*Common JS*/
        Global: './Common/Global',
        UserInfo: './Common/UserInfo',
        Scene: './Common/Scene',
        ConsumptionData: './Common/ConsumptionData',
        External: './Common/External',
        Home: './Common/Home',
        NearField: './Common/NearField',
        Net: './Common/Net',
        PinPad: './Common/PinPad',
        RMS: './Common/RMS',
        ServiceMerchInfo: './Common/ServiceMerchInfo',
        Util: './Common/Util',
        DataBase: './Common/DataBase',
        
        /* DeliveryVoucher */
        DeliveryVocherConsume: './DeliveryVoucher/DeliveryVocherConsume',
       
        /* pay */
       	InputAmount: './pay/InputAmount',
       	MultiPay: './pay/MultiPay',
       	Pay: './pay/Pay',
       	PayAccount: './pay/PayAccount',
       	PayFlow: './pay/PayFlow',
       	PayMethod: './pay/PayMethod',
       	PayReverse: './pay/PayReverse',
       	
       	/* TransactionManage */
       	ConsumptionRecord: './TransactionManage/ConsumptionRecord',
       	ConsumptionRecordSearch: './TransactionManage/ConsumptionRecordSearch',
       	DelVoucherRecord: './TransactionManage/DelVoucherRecord',
       	DelVoucherRecordSearch: './TransactionManage/DelVoucherRecordSearch',
       	OrderDetail: './TransactionManage/OrderDetail',
       	TransactionManageIndex: './TransactionManage/TransactionManageIndex',
       	
       	/* Others Settings Login*/
       	OthersIndex: './Others/OthersIndex',
       	BalanceResult: './Others/BalanceResult',
       	
       	CreateUser: './Others/Settings/CreateUser',
       	ListUserInfo: './Others/Settings/ListUserInfo',
       	ModifyPwd: './Others/Settings/ModifyPwd',
       	PaymentMechanismInfo: './Others/Settings/PaymentMechanismInfo',
       	SetMachineId: './Others/Settings/SetMachineId',
       	SetMerchId: './Others/Settings/SetMerchId',
       	SettingsDownload: './Others/Settings/SettingsDownload',
       	SettingsIndex: './Others/Settings/SettingsIndex',
       	SetTransId: './Others/Settings/SetTransId',
       	SignIn: './Others/Settings/SignIn',
       	TransBatch: './Others/Settings/TransBatch',
       	
       	LoginIndex: './Others/Settings/Login/LoginIndex',
       	
       	/* Platform */
       	Data8583: './Platform/Data8583',
       	PosPrint: './Platform/PosPrint',
       	Android: './Platform/Android'
       	
    }
});

 
require(['Moo', 'Global', 'Scene'], 
	function(Moo, Global, Scene) {
		console.error('1');
		console.log("init start");
		var scene = new Scene();
		new Global();
	}
);