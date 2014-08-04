;
(function() {
  if (window.ListUserInfo) {
    return;
  }

  function reqMoreInfo() {
    var params = {
      "reqMoreInfo": true,
    };
    window.SettingsIndex.gotoListUsers(params);
  }

  function goBack() {
    Scene.goBack("");
  }

  window.ListUserInfo = {
    "reqMoreInfo": reqMoreInfo,
    "goBack": goBack,
  };

})();