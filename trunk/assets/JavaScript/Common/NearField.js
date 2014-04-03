;(function(){
 if (window.NearField) { return }
  
  function processReceivedData(data) {
  	var params = JSON.parse(data)
  	var actionPurpose = params.actionPurpose
  	var payType = params.payType
	if("delVoucher" == actionPurpose) {
		var nextForm = {
			payType : payType,
			field0 : params.field0
		}
		Scene.goBack("DelVoucherWayChosen",nextForm)
	} else if("consumption" == actionPurpose) {
		ConsumptionData.dataForSavingOrder.payType = payType
		ConsumptionData.dataForPayment.payType = payType
		ConsumptionData.dataForPayment.field0 = params.field0
		ConsumptionData.dataForPayment.field1 = params.field1
		ConsumptionData.dataForPayment.field2 = params.field2
		ConsumptionData.dataForPayment.field3 = params.field3
		ConsumptionData.dataForPayment.field4 = params.field4
		if("" == window.util.payTypeCode2Name(payType)
			|| payType == window.util.getPayTypeCode("allinpay_cash")
			|| payType == window.util.getPayTypeCode("coupon")) {
			Scene.goBack("ConsumptionIndex",{})
		} else {
			var nextForm = {
				notPaid : params.notPaid,
				shouldRemoveCurCtrl : true
			}
			Scene.showScene("InputMoney","",nextForm)
		}
	}
  }
  
 window.NearField = {
 	processReceivedData : processReceivedData,
  }
  
})()